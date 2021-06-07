import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

inThisBuild(Seq(
  version := "0.1.0-SNAPSHOT",

  scalaVersion := "2.13.5",

  Global / onChangedBuildSource := ReloadOnSourceChanges
))

lazy val commonSettings = Seq(
  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.3" cross CrossVersion.full),

  resolvers ++=
    ("jitpack" at "https://jitpack.io") ::
    Nil,

  libraryDependencies ++=
    "org.scalatest" %%% "scalatest" % "3.2.0" % Test ::
    Nil,

  scalacOptions --= Seq("-Xfatal-warnings"),
)

lazy val jsSettings = Seq(
  useYarn := true,
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
  scalaJSLinkerConfig ~= { _.withOptimizer(false) },
  Compile / npmDevDependencies += NpmDeps.funpack
)

lazy val localeSettings = Seq(
  zonesFilter := {(z: String) => false},
)

lazy val webSettings = Seq(
  scalaJSUseMainModuleInitializer := true,
  /* scalaJSLinkerConfig ~= { _.withESFeatures(_.withUseECMAScript2015(false)) }, */
  Test / requireJsDomEnv := true,
  webpack / version := "4.43.0",
  startWebpackDevServer / version := "3.11.0",
  webpackDevServerExtraArgs := Seq("--progress", "--color"),
  webpackDevServerPort := 12345,
  fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(),
  fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.config.dev.js"),
  fullOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.config.prod.js"),
)

lazy val api = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("api"))
  .settings(commonSettings)

lazy val apiHttp = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("api-http"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++=
      Deps.cats.effect.value ::
      Deps.awsLambdaJS.value ::
      "com.softwaremill.sttp.tapir" %%% "tapir-core" % "0.18.0-M15" ::
      "com.softwaremill.sttp.tapir" %%% "tapir-json-circe" % "0.18.0-M15" ::
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "0.18.0-M15" ::
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % "0.18.0-M15" ::
      Nil
  )

lazy val eventData = project
  .in(file("event-data"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++=
      Deps.zio.core.value ::
      Nil
  )

lazy val eventPersistency = project
  .dependsOn(eventData)
  .in(file("event-persistency"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++=
      Deps.zio.core.value ::
      Nil
  )

lazy val eventDistributor = project
  .dependsOn(eventData)
  .in(file("event-distributor"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++=
      Deps.zio.core.value ::
      Nil
  )

lazy val webApi = project
  .dependsOn(api.jvm, eventData)
  .in(file("web-api"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++=
      Deps.sloth.value ::
      Deps.zio.core.value ::
      Deps.zio.cats.value ::
      Deps.http4s.server.value ::
      Deps.http4s.dsl.value ::
      Deps.boopickle.value ::
      Nil
  )

lazy val lambdaApi = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin) //, LocalesPlugin, TzdbPlugin)
  .dependsOn(api.js)
  .in(file("lambda-api"))
  .settings(commonSettings, jsSettings, localeSettings)
  .settings(
    webpackEmitSourceMaps in fullOptJS := false,
    webpackConfigFile in fullOptJS := Some(
      baseDirectory.value / "webpack.config.prod.js",
    ),

    libraryDependencies ++=
      Deps.sloth.value ::
      /* Deps.zio.core.value :: */
      /* Deps.zio.cats.value :: */
      Deps.cats.effect.value ::
      "dev.zio" %%% "zio" % "1.0.1" ::
      "dev.zio" %%% "zio-interop-cats" % "2.1.4.0" ::
      Deps.funstack.lambda.value ::
      "com.github.cornerman.chameleon" %%% "chameleon" % "01426c2" ::
      Deps.boopickle.value ::
      Deps.mycelium.core.value ::
      Nil,
  )

lazy val lambdaHttp = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin) //, LocalesPlugin, TzdbPlugin)
  .dependsOn(apiHttp.js)
  .in(file("lambda-http"))
  .settings(commonSettings, jsSettings, localeSettings)
  .settings(
    webpackEmitSourceMaps in fullOptJS := false,
    webpackConfigFile in fullOptJS := Some(
      baseDirectory.value / "webpack.config.prod.js",
    ),

    libraryDependencies ++=
      /* Deps.funstack.lambdaHttp.value :: */
      Nil,
  )

lazy val webClient = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin) //, LocalesPlugin, TzdbPlugin)
  .dependsOn(api.js)
  .in(file("web-client"))
  .settings(commonSettings, jsSettings, localeSettings, webSettings)
  .settings(
    libraryDependencies ++=
      Deps.sloth.value ::
      /* Deps.zio.core.value :: */
      "dev.zio" %%% "zio" % "1.0.1" ::
      Deps.funstack.web.value ::
      "com.github.cornerman.chameleon" %%% "chameleon" % "01426c2" ::
      Deps.boopickle.value ::
      Deps.jsrequests.value ::
      Deps.cuid.value ::
      Deps.outwatch.core.value ::
      Deps.outwatch.zio.value ::
      Deps.mycelium.clientJs.value ::
      Nil,
  )

addCommandAlias("dev", "devInit; devWatchClient; devDestroy")
addCommandAlias("devInit", "webClient/fastOptJS::webpack; webClient/fastOptJS::startWebpackDevServer")
addCommandAlias("devWatchClient", "~webClient/fastOptJS")
addCommandAlias("devDestroy", "webClient/fastOptJS::stopWebpackDevServer")

/* addCommandAlias("dev", "devInit; devWatchAll; devDestroy") // watch all */
/* addCommandAlias("devf", "devInit; devWatchClient; devDestroy") // only watch frontend */
/* addCommandAlias("deva", "devInit; devWatchApi; devDestroy") // only watch backend */
/* addCommandAlias("devInit", "webApi/reStart; webClient/fastOptJS::webpack; webClient/fastOptJS::startWebpackDevServer") */
/* addCommandAlias("devWatchAll", "~; webApi/reStart; webClient/fastOptJS::webpack") */
/* addCommandAlias("devWatchClient", "~webClient/fastOptJS::webpack") */
/* addCommandAlias("devWatchApi", "~webApi/reStart") */
/* addCommandAlias("devDestroy", "webClient/fastOptJS::stopWebpackDevServer; webApi/reStop") */
