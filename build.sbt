name := """torrdigger"""
organization := "com.github.zella"

version := "0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.2"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2"
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.28.0"
libraryDependencies += "io.monix" %% "monix-reactive" % "3.1.0"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "com.github.zella" % "rx-process2" % "0.2.0-BETA2"
libraryDependencies += "org.jsoup" % "jsoup" % "1.12.1"
libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0"


