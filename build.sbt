name := "streams-custom-session"

version := "0.4-SNAPSHOT"

organization := "com.wardziniak"

scalaVersion := "2.12.4"

ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true

useGpg := true
