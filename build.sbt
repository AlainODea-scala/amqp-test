import sbtassembly.Plugin.AssemblyKeys._

name := "amqp-test"

version := "1.0"

organization := "com.alainodea"

scalaVersion := "2.10.2"

assemblySettings

mainClass in assembly := Some("com.alainodea.AMQPTestMain")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-g:vars", "-explaintypes", "-optimise", "-encoding", "UTF8")

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

libraryDependencies += "com.rabbitmq" % "amqp-client" % "3.2.1"

libraryDependencies += "com.github.nscala-time" % "nscala-time_2.10" % "0.6.0"
