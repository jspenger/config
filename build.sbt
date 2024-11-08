ThisBuild / scalaVersion := "3.3.1"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.jspenger"
ThisBuild / licenses := List(
  "MIT" -> url("https://opensource.org/licenses/MIT")
)
ThisBuild / developers := List(
  Developer(
    id    = "jspenger",
    name  = "Jonas Spenger",
    email = "@jonasspenger",
    url   = url("https://github.com/jspenger"),
  )
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "config",
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
  )

lazy val examples = project
  .in(file("config-examples"))
  .settings(
    name := "config-examples",
    publish / skip := true,
  )
  .dependsOn(root)