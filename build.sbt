name := "bytestring"

crossScalaVersions := Seq("3.2.0", "2.13.10", "2.12.17", "2.11.12", "2.10.7")
ThisBuild / scalaVersion := crossScalaVersions.value.head

// TODO: Replace with ScalaModulePlugin.enableOptimizer when https://github.com/scala/sbt-scala-module/pull/155 gets released
lazy val enableOptimizer: Setting[_] = Compile / compile / scalacOptions ++= {
  val Ver = """(\d+)\.(\d+)\.(\d+).*""".r
  val Ver(epic, maj, min) = scalaVersion.value
  (epic, maj.toInt, min.toInt) match {
    case ("2", m, _) if m < 12 => Seq("-optimize")
    case ("2", 12, n) if n < 3 => Seq("-opt:l:project")
    case ("2", _, _)           => Seq("-opt:l:inline", "-opt-inline-from:" + scalaModuleEnableOptimizerInlineFrom.value)
    case ("3", _, _) =>
      Nil // Optimizer not yet available for Scala3, see https://docs.scala-lang.org/overviews/compiler-options/optimizer.html
  }
}

def scalaTestVersion(scalaVersion: String): String = CrossVersion.partialVersion(scalaVersion) match {
  case Some((2, n)) if n <= 11 =>
    "3.2.9"
  case _ =>
    "3.2.14"
}

def scalaTestPlusScalaCheckModule(scalaVersion: String): ModuleID = CrossVersion.partialVersion(scalaVersion) match {
  case Some((2, n)) if n <= 11 =>
    "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test
  case _ =>
    "org.scalatestplus" %% "scalacheck-1-17" % "3.2.14.0" % Test
}

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
      "org.scalatest" %% "scalatest" % scalaTestVersion(scalaVersion.value) % Test,
      scalaTestPlusScalaCheckModule(scalaVersion.value),
      "commons-codec" % "commons-codec" % "20041127.091804" % Test
    ),
    Test / unmanagedSourceDirectories ++= {
      val sourceDir = (Test / sourceDirectory).value
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 12 => Seq(sourceDir / "scala-2.12-")
        case _                       => Nil
      }
    },
    enableOptimizer
  )

lazy val benchmark = project
  .in(file("benchmark"))
  .enablePlugins(JmhPlugin)
  .settings(
    publish / skip := true,
    publishLocal / skip := true
  )
  .dependsOn(byteString)
