package com.wardziniak.kafka.streams.session

import com.wardziniak.kafka.streams.session.ExtendedKStream.CustomSessionTransformer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream._
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.state.{KeyValueStore, Stores}

import scala.language.implicitConversions

class ExtendedKStream[K, V](kStream: KStream[K,V]) {

  def aggregate[AGG](
    initializer: Initializer[AGG],
    aggregator: Aggregator[_ >: K, _ >: V, AGG],
    customSession: CustomSession[K, V, AGG],
    keySerde: Serde[K],
    aggSerde: Serde[AGG]
    //materialized: Materialized[K, VR, KeyValueStore[Bytes, Array[Byte]]]
  )(implicit builder: StreamsBuilder): KStream[K, AGG] = {
    val storeName = "stateStoreName"
    val internalStoreSupplier = Stores.persistentKeyValueStore(storeName)
    val internalStoreBuilder = Stores.keyValueStoreBuilder(internalStoreSupplier, keySerde, aggSerde)
    builder.addStateStore(internalStoreBuilder)
    val valueTransformerSupplier: ValueTransformerWithKeySupplier[K, V, AGG] = () => CustomSessionTransformer(storeName)
    kStream.transformValues(valueTransformerSupplier, storeName)
  }
}

object ExtendedKStream {
  implicit def asExtendedKStream[K, V](kstream: KStream[K, V]): ExtendedKStream[K, V] = new ExtendedKStream(kstream)

  private case class CustomSessionTransformer[K, V, AGG](sessionStoreName: String) extends ValueTransformerWithKey[K, V, AGG] {
    override def init(context: ProcessorContext): Unit = {}

    override def transform(readOnlyKey: K, value: V): AGG = ???

    override def close(): Unit = {}
  }
}
