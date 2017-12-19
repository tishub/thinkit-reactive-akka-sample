name := """thinkit-reactive-akka-sample"""

version := "1.0"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.7",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.7",
  "com.typesafe.akka" %% "akka-stream" % "2.5.7",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.7" % Test,
  "ch.qos.logback" % "logback-classic" % "1.1.3")

fork in run := true
