import sbt._
import Keys._

import com.typesafe.sbteclipse.plugin.EclipsePlugin
import EclipsePlugin.{ EclipseKeys, EclipseProjectFlavor}
import net.virtualvoid.sbt.graph.{ Plugin => GraphPlugin }

// ----------------------------------------------------------------------------

trait Default {
  private val default =
    Defaults.defaultSettings ++
    EclipsePlugin.settings ++
    GraphPlugin.graphSettings ++ Seq(
      organization := "com.ferega.props",
      version      := "0.0.0-SNAPSHOT",
      scalaVersion := "2.11.1"
    )

  val scala =
    default ++ Seq(
      EclipseKeys.projectFlavor := EclipseProjectFlavor.Scala,
      unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil,
      unmanagedSourceDirectories in Test := (scalaSource in Test).value :: Nil,
      scalacOptions := Seq(
        "-deprecation",
        "-encoding", "UTF-8",
        "-feature",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-language:postfixOps",
        "-optimise",
        "-unchecked",
        "-Xcheckinit",
        "-Xlint",
        "-Xno-uescape",
        "-Xverify",
        "-Yclosure-elim",
        "-Ydead-code",
        "-Yinline"
      )
    )

  val java =
    default ++ Seq(
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
    
  val publishing = Seq()
}

// ----------------------------------------------------------------------------

trait Dependencies {
  val jUnit          = "junit"          %  "junit"           % "4.11"     % "test"
  val jUnitInterface = "com.novocode"   %  "junit-interface" % "0.11-RC1" % "test"
  val reflectApi     = "org.scala-lang" %  "scala-reflect"   % "2.11.1"
  val scalaTest      = "org.scalatest"  %% "scalatest"       % "2.2.0"    % "test"
}

// ----------------------------------------------------------------------------

object PropsLoaderBuild extends Build with Default with Dependencies {
  lazy val javaApi  = Project(
    "java-api",
    file("JavaApi"),
    settings = java ++ publishing ++ Seq(
      name := "PropsLoader-JavaApi",
      libraryDependencies ++= Seq(jUnit, jUnitInterface)
    )
  )

  lazy val scalaApi = Project(
    "scala-api",
    file("ScalaApi"),
    settings = scala ++ publishing ++ Seq(
      name := "PropsLoader-ScalaApi",
      libraryDependencies ++= Seq(reflectApi, scalaTest)
    )
  ) dependsOn(javaApi)
}
