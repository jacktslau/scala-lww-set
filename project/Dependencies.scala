import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4"
  lazy val playScalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2"
  lazy val redis = "net.debasishg" %% "redisclient" % "3.4"

  lazy val gatlingCharts    = "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.0"
  lazy val gatlingFramework = "io.gatling"            % "gatling-test-framework"    % "2.3.0"
}
