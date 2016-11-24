name := """thinkit-reactive-akka-sample"""

version := "1.0"

scalaVersion := "2.12.0"

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.14",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.14",
  "ch.qos.logback" % "logback-classic" % "1.1.3")

fork in run := true
