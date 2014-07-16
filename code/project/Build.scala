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
      version      := "0.0.6",
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

  val publishing = Seq(
    publishTo := Some(
      if (version.value endsWith "-SNAPSHOT") {
        Opts.resolver.sonatypeSnapshots
      } else {
        Opts.resolver.sonatypeStaging
      }
    ),
    javacOptions in (Compile, doc) := Seq(),
    crossScalaVersions      := Seq("2.11.1", "2.10.4"),
    publishMavenStyle       := true,
    publishArtifact in Test := false,
    pomIncludeRepository    := { _ => false },
    licenses                += ("MIT", url("http://opensource.org/licenses/MIT")),
    homepage                := Some(url("https://github.com/tferega/PropsLoader/")),
    credentials             += Credentials(Path.userHome / ".config" / "tferega.credentials"),
    startYear               := Some(2014),
    scmInfo                 := Some(ScmInfo(url("https://github.com/tferega/PropsLoader/tree/0.0.5"), "scm:git:https://github.com/tferega/PropsLoader.git")),
    pomExtra                ~= (_ ++ {Developers.toXml})
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
