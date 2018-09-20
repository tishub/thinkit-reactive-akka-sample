name := """thinkit-reactive-akka-sample"""
version := "1.0"
scalaVersion := "2.12.6"
val akkaHttpVersion = "10.1.5"
val akkaVersion     = "2.5.16"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"           % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"           % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
  "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
  "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
  "ch.qos.logback" % "logback-classic" % "1.1.3")

scalacOptions ++= Seq("-feature")
//fork in run := true

