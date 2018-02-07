import Dependencies._

lazy val commonSettings = Seq(
  organization := "crdt.example",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.2"
)

lazy val root = (project in file("."))
  .aggregate(core)

lazy val core = (project in file("crdt-core"))
  .settings(
    commonSettings,
    libraryDependencies += scalaTest % Test
  )
