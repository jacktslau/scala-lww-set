import Dependencies._


lazy val commonSettings = Seq(
  organization := "crdt",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.2"
)

lazy val root = (project in file("."))
  .aggregate(core, server)
  .settings(
    name := "crdt",
    aggregate in update := false
  )

lazy val core = (project in file("crdt-core"))
  .settings(
    commonSettings,
    name := "crdt-core",
    libraryDependencies += scalaTest % Test
  )

lazy val server = (project in file("crdt-server"))
    .enablePlugins(PlayScala)
    .settings(
      commonSettings,
      name := "crdt-server",
      javaOptions in Test += "-Dconfig.file=conf/application.test.conf",
      javaOptions in Test += "-Dlogger.file=conf/logback.test.xml",
      libraryDependencies ++= Seq(
        guice,
        redis,
        playScalaTest % Test
      )
    ).dependsOn(core)

lazy val gatling = (project in file("gatling"))
  .enablePlugins(GatlingPlugin)
  .settings(
    commonSettings,
    name := "gatling",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      gatlingFramework % "test,it",
      gatlingCharts % "test,it"
    )
  ).dependsOn(core, server)