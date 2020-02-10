import sbt.Keys._
import sbt.{Developer, ScmInfo, URL, url}
import sbt.librarymanagement.DependencyBuilders
import sbt.Keys._
import sbt._

object Common {

  lazy val Settings = Seq(
    name                  := "streams-custom-session",
    organization          := "com.wardziniak",
    version               := Versions.StreamsCustomSession,
    organizationName      := "com.wardziniak",
    scalaVersion          := Versions.Scala,
    organizationHomepage  := Some(url("http://wardziniak.com")),
    scmInfo               := Some(ScmInfo(url("https://github.com/wardziniak/streams-custom-session"), "scm:git@github.com:wardziniak/streams-custom-session.git")),
    developers            := List(
      Developer(
        id    = "wardziniak",
        name  = "Bartosz Wardziński",
        email = "bwardziniak@yahoo.pl",
        url   = url("http://wardziniak.com")
      )
    ),
    description           := "Some description of project",
    licenses              := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage              := Some(url("https://github.com/wardziniak/streams-custom-session")),
    pomIncludeRepository  := { _ => false },
    publishTo             := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle     := true,
    libraryDependencies   ++= Dependencies.StreamsCustomSessionDep
  )
}
