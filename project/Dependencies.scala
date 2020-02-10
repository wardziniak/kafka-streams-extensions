import sbt._

object Dependencies {

  val KafkaStreamsScala: ModuleID = "org.apache.kafka" %% "kafka-streams-scala" % Versions.Kafka
  val KafkaStreamsTestUtils: ModuleID = "org.apache.kafka" % "kafka-streams-test-utils" % Versions.Kafka % Test


  val ScalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.ScalaTest % Test

  val StreamsCustomSessionDep: Seq[ModuleID] = Seq(KafkaStreamsScala, KafkaStreamsTestUtils, ScalaTest)
}
