import sbt.Keys.version
import sbt.{SettingKey, ThisBuild}

object Versions {

  val StreamsCustomSession: SettingKey[String] = version in ThisBuild
  val Scala = "2.12.4"

  val Kafka = "2.4.0"
  val ScalaTest = "3.0.8"
}
