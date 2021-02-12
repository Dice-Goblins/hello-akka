enablePlugins(JavaAppPackaging)

name := "hello-akka"
version := "0.1"
scalaVersion := "2.13.4"
organization := "com.dicegoblins"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaVersion = "2.6.8"
  val akkaHttpVersion = "10.2.3"
  val circeVersion = "0.13.0"
  val akkaHttpCirceVersion = "1.35.3"
  val logbackVersion = "1.2.3"

  Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "io.circe"          %% "circe-core" % circeVersion,
    "io.circe"          %% "circe-generic" % circeVersion,
    "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback"    % "logback-classic" % logbackVersion
  )
}

Revolver.settings