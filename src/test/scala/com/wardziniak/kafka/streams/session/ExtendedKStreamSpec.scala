package com.wardziniak.kafka.streams.session

import java.util
import java.util.Properties

import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{StreamsConfig, TestInputTopic, TestOutputTopic, TopologyTestDriver}
import org.scalatest.{Matchers, WordSpec}

class ExtendedKStreamSpec extends WordSpec with Matchers {

  "ExtendedKStream" should {
    "return KStream" in {
      val InputTopic = "input"
      val OutputTopic = "output"

      implicit val builder = new StreamsBuilder()
      val props = new Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ImportSchedulerTopologyBuilderSpec1")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")

      import org.apache.kafka.streams.scala.Serdes.String
      import org.apache.kafka.streams.scala.ImplicitConversions.consumedFromSerde
      import com.wardziniak.kafka.streams.session.ExtendedKStream._

      val output: KStream[String, String] = builder.stream[String, String](InputTopic).aggregate[String](() => "", (key, value, agg) => s"${agg}_$value", (_, _, agg) => agg.length > 10, Serdes.String, Serdes.String)
      import org.apache.kafka.streams.scala.ImplicitConversions.producedFromSerde
      output.to(OutputTopic)

      val testDriver = new TopologyTestDriver(builder.build(), props)

      val inputTopic: TestInputTopic[String, String] = testDriver.createInputTopic[String, String](InputTopic, Serdes.String.serializer(), Serdes.String.serializer())

      inputTopic.pipeInput("k1", "value1")
      inputTopic.pipeInput("k1", "value2")
      inputTopic.pipeInput("k2", "valuevalue2")
      inputTopic.pipeInput("k1", "value")

      val outputTopic: TestOutputTopic[String, String] = testDriver.createOutputTopic[String, String](OutputTopic, Serdes.String.deserializer(), Serdes.String.deserializer())

      val outputMap: util.Map[String, String] = outputTopic.readKeyValuesToMap()
      outputMap.get("k1") shouldBe "value1value2"
    }
  }

}
