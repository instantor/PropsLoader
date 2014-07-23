import sbt._
import Keys._

import com.instantor.plugin.InstantorPlugin.instantorSettings
import com.typesafe.sbteclipse.plugin.EclipsePlugin.{ EclipseKeys, EclipseProjectFlavor}

trait Default {
  private val default =
    instantorSettings ++ Seq(
      organization := "com.instantor.props",
      version      := "0.1.2",
      scalaVersion := "2.11.1",
      unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil,
      unmanagedSourceDirectories in Test := (scalaSource in Test).value :: Nil
    )

  val scala =
    default

  val java =
    default ++ Seq(
      EclipseKeys.projectFlavor := EclipseProjectFlavor.Java,
      autoScalaLibrary          := false,
      crossPaths                := false,
      testOptions               += Tests.Argument(TestFrameworks.JUnit, "-v", "-q")
    )

  val publishing = Seq(
    crossScalaVersions      := Seq("2.11.1", "2.10.4"),
    publishArtifact in Test := false
  )
}

// ----------------------------------------------------------------------------

trait Dependencies {
  lazy val reflectApi = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }
  lazy val jUnitInterface = "com.novocode"  %  "junit-interface" % "0.11-RC1" % "test"
  lazy val scalaTest      = "org.scalatest" %% "scalatest"       % "2.2.0"    % "test"
}

// ----------------------------------------------------------------------------

object PropsLoaderBuild extends Build with Default with Dependencies {
  lazy val javaApi  = Project(
    "java-api",
    file("JavaApi"),
    settings = java ++ publishing ++ Seq(
      name := "PropsLoader-JavaApi",
      libraryDependencies ++= Seq(jUnitInterface)
    )
  )

  lazy val scalaApi = Project(
    "scala-api",
    file("ScalaApi"),
    settings = scala ++ publishing ++ Seq(
      name := "PropsLoader-ScalaApi",
      libraryDependencies ++= Seq(reflectApi.value, scalaTest)
    )
  ) dependsOn(javaApi)
}
