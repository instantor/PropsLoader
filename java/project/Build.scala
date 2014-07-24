import sbt._
import Keys._

import com.typesafe.sbteclipse.plugin.EclipsePlugin.{ EclipseKeys, EclipseProjectFlavor, EclipseCreateSrc}
import com.instantor.plugin.InstantorPlugin

// ----------------------------------------------------------------------------

trait Default {
  private lazy val default =
    Defaults.defaultSettings ++ 
    InstantorPlugin.instantorSettings ++ Seq(
      organization := "com.instantor.props",
      version      := "0.3.1"
    )

  lazy val javaSettings =
    default ++
    publishing ++ Seq(
      EclipseKeys.projectFlavor := EclipseProjectFlavor.Java,
      autoScalaLibrary          := false,
      crossPaths                := false,
      testOptions               += Tests.Argument(TestFrameworks.JUnit, "-v", "-q"),
      unmanagedSourceDirectories in Compile := (javaSource in Compile).value :: Nil,
      unmanagedSourceDirectories in Test    := (javaSource in Test).value :: Nil
    )

  lazy val publishing = Seq(
    publishTo := Some(
      if (version.value endsWith "-SNAPSHOT") {
        InstantorPlugin.InstantorSnapshots
      } else {
        InstantorPlugin.InstantorReleases
      }
    ),
    javacOptions in (Compile, doc) := Nil,
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
