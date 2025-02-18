ThisBuild / tlBaseVersion := "0.6"
ThisBuild / organization := "io.chrisdavenport"
ThisBuild / organizationName := "Christopher Davenport"
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(
  tlGitHubDev("christopherdavenport", "Christopher Davenport")
)
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / tlSonatypeUseLegacyHost := true

ThisBuild / crossScalaVersions := Seq("2.12.15", "2.13.8", "3.1.2")

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

ThisBuild / versionScheme := Some("early-semver")


val catsV = "2.8.0"
val catsEffectV = "3.3.13"
val catsCollectionV = "0.9.3"

val munitV = "0.7.25"
val munitCEV = "1.0.7"

lazy val mules = tlCrossRootProject
  .aggregate(core, caffeine, reload, noop, bench)

lazy val bench = project.in(file("modules/bench"))
  .enablePlugins(JmhPlugin)
  .disablePlugins(MimaPlugin)
  .enablePlugins(NoPublishPlugin)
  .dependsOn(core.jvm, caffeine)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core"))
  .settings(
    name := "mules",
    libraryDependencies ++= Seq(
      "org.typelevel"               %%% "cats-core"                  % catsV,
      "org.typelevel"               %%% "cats-effect"                % catsEffectV,
      "io.chrisdavenport"           %%% "mapref"                     % "0.2.1",
    ),
  ).settings(testDeps)
  .jsSettings(
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule)},
  )

lazy val caffeine = project.in(file("modules/caffeine"))
  .dependsOn(core.jvm)
  .settings(
    name := "mules-caffeine",
    libraryDependencies ++= Seq(
      "com.github.ben-manes.caffeine" % "caffeine" % "2.9.3"
    ),
  ).settings(testDeps)

lazy val noop = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/noop"))
  .dependsOn(core)
  .settings(
    name := "mules-noop"
  ).settings(testDeps)
  .jsSettings(
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule)},
  )

lazy val reload = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/reload"))
  .dependsOn(core)
  .settings(
    name := "mules-reload",
    libraryDependencies ++= Seq(
      "org.typelevel"               %%% "cats-collections-core"      % catsCollectionV
    ),
  ).settings(testDeps)
  .jsSettings(
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule)},
  )

lazy val testDeps = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-effect-laws" % catsEffectV % Test,
    "org.scalameta" %%% "munit" % munitV % Test,
    "org.scalameta" %%% "munit-scalacheck" % munitV % Test,
    "org.typelevel" %%% "munit-cats-effect-3" % munitCEV % Test,
  )
)
