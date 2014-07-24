import sbt._
import Keys._

import com.typesafe.sbteclipse.plugin.EclipsePlugin
import EclipsePlugin.{ EclipseKeys, EclipseProjectFlavor, EclipseCreateSrc}
import net.virtualvoid.sbt.graph.{ Plugin => GraphPlugin }

// ----------------------------------------------------------------------------

trait Default {
  private lazy val default =
    Defaults.defaultSettings ++
    EclipsePlugin.settings ++
    GraphPlugin.graphSettings ++ Seq(
      organization := "com.instantor.props",
      version      := "0.3.0",
      scalaVersion := "2.11.1"
    )

  lazy val javaSettings =
    default ++
    publishing ++ Seq(
      EclipseKeys.projectFlavor := EclipseProjectFlavor.Java,
      autoScalaLibrary          := false,
      crossPaths                := false,
      testOptions               += Tests.Argument(TestFrameworks.JUnit, "-v", "-q"),
      unmanagedSourceDirectories in Compile := (javaSource in Compile).value :: Nil,
      unmanagedSourceDirectories in Test    := (javaSource in Test).value :: Nil,
      javacOptions := Seq(
        "-deprecation",
        "-encoding", "UTF-8",
        "-Xlint:all"
      )
    )

  lazy val publishing = Seq(
    publishTo := Some(
      if (version.value endsWith "-SNAPSHOT") {
        Opts.resolver.sonatypeSnapshots
      } else {
        Opts.resolver.sonatypeStaging
      }
    ),
    javacOptions in (Compile, doc) := Nil,
    crossScalaVersions      := Seq("2.11.1"),
    publishArtifact in Test := false
  )
}

// ----------------------------------------------------------------------------

trait Dependencies {
  lazy val slf4j = "org.slf4j" %  "slf4j-api" % "1.7.7"

  lazy val jUnitInterface = "com.novocode"  %  "junit-interface" % "0.11-RC1" % "test"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.1.2" % "test"
}

// ----------------------------------------------------------------------------

object PropsLoaderBuild extends Build with Default with Dependencies {
  lazy val api = Project(
    "api",
    file("Api"),
    settings = javaSettings ++ Seq(
      name := "PropsLoader-Api",
      unmanagedSourceDirectories in Test := Nil
    )
  )

  lazy val core = Project(
    "core",
    file("Core"),
    settings = javaSettings ++ Seq(
      name := "PropsLoader-Core",
      libraryDependencies ++= Seq(slf4j, jUnitInterface, logback),
      unmanagedSourceDirectories in Test := (javaSource in Test).value :: Nil,
      EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
    )
  ).dependsOn(api)
}
