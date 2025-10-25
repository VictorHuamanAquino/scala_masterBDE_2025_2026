import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.{MergeStrategy, PathList}

ThisBuild / organization := "com.ntic"
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.17"

lazy val root = (project in file("."))
  .settings(
    name := "bde_scala_2025_2026",
    assembly / mainClass := Some("com.ntic.Main"),
    assembly / assemblyJarName := s"${name.value}-${version.value}.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  )

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "com.typesafe" % "config" % "1.4.5"
)
