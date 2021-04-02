import Options._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

inThisBuild(Seq(
  version := "0.1.0-SNAPSHOT",

  scalaVersion := "2.13.3"
))

lazy val commonSettings = Seq(
  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full),

  resolvers ++=
    ("jitpack" at "https://jitpack.io") ::
    Nil,

  libraryDependencies ++=
    "org.scalatest" %%% "scalatest" % "3.2.0" % Test ::
    Nil,

  scalacOptions ++= CrossVersion.partialVersion(scalaVersion.value).map(v =>
    allOptionsForVersion(s"${v._1}.${v._2}", true)
  ).getOrElse(Nil),
  scalacOptions in (Compile, console) ~= (_.diff(badConsoleFlags))
)

lazy val jsSettings = Seq(
  useYarn := true,
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
)

lazy val localeSettings = Seq(
  zonesFilter := {(z: String) => false},
)

lazy val webSettings = Seq(
  scalaJSUseMainModuleInitializer := true,
  scalaJSLinkerConfig ~= { _.withESFeatures(_.withUseECMAScript2015(false)) },
  requireJsDomEnv in Test := true,
  version in webpack := "4.43.0",
  version in startWebpackDevServer := "3.11.0",
  webpackDevServerExtraArgs := Seq("--progress", "--color"),
  webpackDevServerPort := 12345,
  webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack.config.dev.js"),
  npmDevDependencies in Compile ++= NpmDeps.webpackDependencies,
  webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),
)

lazy val webClient = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin) //, LocalesPlugin, TzdbPlugin)
  .in(file("web-client"))
  .settings(commonSettings, jsSettings, localeSettings, webSettings)
  .settings(
    libraryDependencies ++=
      Deps.outwatch.core.value ::
      Nil
  )

lazy val root = project
  .in(file("."))
  .settings(
    skip in publish := true,
  )
  .aggregate(webClient)


addCommandAlias("dev", "devInit; devWatchAll; devDestroy") // watch all
addCommandAlias("devInit", "webClient/fastOptJS::webpack; webClient/fastOptJS::startWebpackDevServer")
addCommandAlias("devWatchAll", "~; webClient/fastOptJS::webpack")
addCommandAlias("devDestroy", "webClient/fastOptJS::stopWebpackDevServer")
