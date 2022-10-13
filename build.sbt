name := "bytestring"

crossScalaVersions := Seq("2.13.12", "2.12.18", "3.3.1")
ThisBuild / scalaVersion := crossScalaVersions.value.head

def warningFlags(scalaVersion: String): Seq[String] = CrossVersion.partialVersion(scalaVersion) match {
  case Some((3, _)) =>
    Seq("-Wconf:cat=deprecated")
  case Some((2, n)) if n >= 12 =>
    Seq("-Wconf:cat=deprecated")
  case _ =>
    Nil
}

lazy val byteString = project
  .in(file("."))
  .settings(ScalaModulePlugin.scalaModuleSettings)
  .settings(ScalaModulePlugin.scalaModuleOsgiSettings)
  .settings(
    name := "bytestring",
    scalaModuleAutomaticModuleName := Some("scala.immutable.collection.ByteString"),
    OsgiKeys.exportPackage := Seq(s"scala.bytestring.*;version=${version.value}"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "org.scalatestplus" %% "scalacheck-1-17" % "3.2.15.0" % Test,
      "commons-codec" % "commons-codec" % "20041127.091804" % Test
    ),
    Compile / unmanagedSourceDirectories ++= {
      val sourceDir = (Compile / sourceDirectory).value
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))            => Seq(sourceDir / "scala-2.13+")
        case Some((2, n)) if n >= 13 => Seq(sourceDir / "scala-2.13+")
        case _                       => Nil
      }
    },
    // Adds a `src/test/scala-2.13+` source directory for code shared
    // between Scala 2.13 and Scala 3
    Test / unmanagedSourceDirectories ++= {
      val sourceDir = (Test / sourceDirectory).value
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))            => Seq(sourceDir / "scala-2.13+")
        case Some((2, n)) if n >= 13 => Seq(sourceDir / "scala-2.13+")
        case _                       => Nil
      }
    },
    ScalaModulePlugin.enableOptimizer
  )

lazy val benchmark = project
  .in(file("benchmark"))
  .enablePlugins(JmhPlugin)
  .settings(
    publish / skip := true,
    publishLocal / skip := true
  )
  .dependsOn(byteString)
