package com.wardziniak.kafka.streams.session

import java.util.Properties

import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{StreamsConfig, TestInputTopic, TestOutputTopic, TopologyTestDriver}
import org.scalatest.{Matchers, WordSpec}

class ExtendedKStreamSpec extends WordSpec with Matchers {

  val InputTopic = "input"
  val OutputTopic = "output"

  "ExtendedKStream" should {

    "return KStream" in {
      import com.wardziniak.kafka.streams.session.ExtendedKStream._
      import org.apache.kafka.streams.scala.ImplicitConversions.consumedFromSerde
      import org.apache.kafka.streams.scala.Serdes.String

      implicit val builder: StreamsBuilder = new StreamsBuilder()
      val output = builder.stream[String, String](InputTopic).aggregate[String](() => "", (_, value, agg) => s"${agg}_$value", (_, _, agg) => agg.length > 10, Serdes.String, Serdes.String)
      assert(output.isInstanceOf[KStream[String, String]], "Result of aggregation should be instance of KStream[String, String]")
    }

    "make aggregation with Custom Session" in {

      implicit val builder: StreamsBuilder = new StreamsBuilder()
      val props = new Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ExtendedKStreamSpec")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")

      import com.wardziniak.kafka.streams.session.ExtendedKStream._
      import org.apache.kafka.streams.scala.ImplicitConversions.consumedFromSerde
      import org.apache.kafka.streams.scala.Serdes.String

      val output: KStream[String, String] = builder.stream[String, String](InputTopic).aggregate[String](() => "", (_, value, agg) => s"${agg}_$value", (_, _, agg) => agg.length > 10, Serdes.String, Serdes.String)
      import org.apache.kafka.streams.scala.ImplicitConversions.producedFromSerde
      output.to(OutputTopic)

      val testDriver = new TopologyTestDriver(builder.build(), props)

      val inputTopic: TestInputTopic[String, String] = testDriver.createInputTopic[String, String](InputTopic, Serdes.String.serializer(), Serdes.String.serializer())
      val outputTopic: TestOutputTopic[String, String] = testDriver.createOutputTopic[String, String](OutputTopic, Serdes.String.deserializer(), Serdes.String.deserializer())

      inputTopic.pipeInput("k1", "value1")
      val out1 = outputTopic.readRecord()
      out1.value() shouldBe null

      inputTopic.pipeInput("k1", "value2")
      val out2 = outputTopic.readRecord()
      out2.value() shouldBe "_value1_value2"

      inputTopic.pipeInput("k2", "valuevalue2")
      val out3 = outputTopic.readRecord()
      out3.value() shouldBe "_valuevalue2"

      inputTopic.pipeInput("k1", "value3")
      val out4 = outputTopic.readRecord()
      out4.value() shouldBe null
      testDriver.close()
    }
  }

}
