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
      organization := "com.ferega",
      version      := "0.0.0-SNAPSHOT",
      scalaVersion := "2.11.1"
    )

  val scala =
    default ++ Seq(
      EclipseKeys.projectFlavor := EclipseProjectFlavor.Scala,
      unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil,
      unmanagedSourceDirectories in Test    := Nil,
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
      unmanagedSourceDirectories in Compile := (javaSource in Compile).value :: Nil,
      unmanagedSourceDirectories in Test    := Nil,
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
}

// ----------------------------------------------------------------------------

object LSysBuild extends Build with Default with Dependencies {
  lazy val javaApi  = Project(
    "java-api",
    file("JavaApi"),
    settings = java ++ publishing ++ Seq(
      name := "PropsLoader-JavaApi"
    )
  )

  lazy val scalaApi = Project(
    "scala-api",
    file("ScalaApi"),
    settings = scala ++ publishing ++ Seq(
      name := "PropsLoad-ScalaApi"
    )
  ) dependsOn(javaApi)
}
