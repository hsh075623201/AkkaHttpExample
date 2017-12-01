name := "AkkaHttpExample"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
"com.typesafe.akka" %% "akka-http" % "10.0.10",
"com.typesafe.akka" %% "akka-stream" % "2.5.4" ,
"com.typesafe.akka" %% "akka-actor"  % "2.5.4",
 "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10",
"io.spray" %% "spray-json" % "1.3.2"
)
    