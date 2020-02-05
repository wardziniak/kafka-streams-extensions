ThisBuild / organizationName := "Bartosz Wardzinski"
ThisBuild / organizationHomepage := Some(url("http://wardziniak.com"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/wardziniak/streams-custom-session"),
    "scm:git@github.com:wardziniak/streams-custom-session.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "wardziniak",
    name  = "Bartosz Wardziński",
    email = "bwardziniak@yahoo.pl",
    url   = url("http://wardziniak.com")
  )
)

ThisBuild / description := "Some descripiton about your project."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/wardziniak/streams-custom-session"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true