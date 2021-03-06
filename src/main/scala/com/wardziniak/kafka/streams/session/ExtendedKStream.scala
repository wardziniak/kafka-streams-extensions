package com.wardziniak.kafka.streams.session

import com.wardziniak.kafka.streams.session.ExtendedKStream.CustomSessionTransformer
import org.apache.kafka.streams.kstream._
import org.apache.kafka.streams.kstream.internals.MaterializedInternal
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, StreamsBuilder}
import org.apache.kafka.streams.state.{KeyValueBytesStoreSupplier, KeyValueStore, Stores}

import scala.language.implicitConversions

class ExtendedKStream[K, V](kStream: KStream[K,V]) {

  def aggregate[AGG >: Null](
    initializer: Initializer[AGG],
    aggregator: Aggregator[K, V, AGG],
    closeSessionPredicate: CloseSessionPredicate[K, V, AGG])(implicit builder: StreamsBuilder, materialized: Materialized[K, AGG, ByteArrayKeyValueStore]): KStream[K, AGG] = {
    val internalMat = new MaterializedInternal[K, AGG, ByteArrayKeyValueStore](materialized)
    val storeName = internalMat.storeName()
    val internalStoreSupplier: KeyValueBytesStoreSupplier = Stores.persistentKeyValueStore(storeName)
    val internalStoreBuilder = Stores.keyValueStoreBuilder(internalStoreSupplier, internalMat.keySerde(), internalMat.valueSerde())
    builder.addStateStore(internalStoreBuilder)
    val valueTransformerSupplier: ValueTransformerWithKeySupplier[K, V, AGG] = () => CustomSessionTransformer(sessionStoreName = storeName, initializer = initializer, aggregator = aggregator, closeSessionPredicate = closeSessionPredicate)
    kStream.transformValues(valueTransformerSupplier, storeName)
  }
}

object ExtendedKStream {

  implicit def asExtendedKStream[K, V](kstream: KStream[K, V]): ExtendedKStream[K, V] = new ExtendedKStream(kstream)

  private case class CustomSessionTransformer[K, V, AGG >: Null](sessionStoreName: String, initializer: Initializer[AGG], aggregator: Aggregator[_ >: K, _ >: V, AGG], closeSessionPredicate: CloseSessionPredicate[K, V, AGG]) extends ValueTransformerWithKey[K, V, AGG] {

    protected var sessionCache: KeyValueStore[K, AGG] = _

    override def init(context: ProcessorContext): Unit = {
      sessionCache = context.getStateStore(sessionStoreName).asInstanceOf[KeyValueStore[K, AGG]]
    }

    override def transform(readOnlyKey: K, value: V): AGG = {
      val oldAgg: AGG = Option(sessionCache.get(readOnlyKey))
        .getOrElse(initializer())
      val newAgg = aggregator(readOnlyKey, value, oldAgg)

      if (closeSessionPredicate(readOnlyKey, value, newAgg)) {
        sessionCache.delete(readOnlyKey)
        newAgg
      }
      else {
        sessionCache.put(readOnlyKey, newAgg)
        null
      }
    }

    override def close(): Unit = {}
  }
}
