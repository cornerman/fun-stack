import Options._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

inThisBuild(Seq(
  version := "0.1.0-SNAPSHOT",

  scalaVersion := "2.13.3",

  Global / onChangedBuildSource := ReloadOnSourceChanges
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
  webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),
  webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack.config.dev.js"),
  webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack.config.prod.js"),
  npmDevDependencies in Compile ++= NpmDeps.webpackDependencies,
)

lazy val api = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("api"))
  .settings(commonSettings)

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
    libraryDependencies ++=
      Deps.sloth.value ::
      /* Deps.zio.core.value :: */
      /* Deps.zio.cats.value :: */
      Deps.cats.effect.value ::
      "dev.zio" %%% "zio" % "1.0.1" ::
      "dev.zio" %%% "zio-interop-cats" % "2.1.4.0" ::
      Deps.boopickle.value ::
      Deps.base64.value ::
      Deps.awsSdkJS.value ::
      Deps.awsLambdaJS.value ::
      Nil,

    npmDependencies in Compile ++=
      NpmDeps.awsSdk ::
      Nil
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
      Deps.boopickle.value ::
      Deps.jsrequests.value ::
      Deps.cuid.value ::
      Deps.outwatch.core.value ::
      Deps.outwatch.zio.value ::
      Deps.base64.value ::
      Deps.awsSdkJS.value ::
      Deps.newtype.value ::
      Nil,

    npmDependencies in Compile ++=
      NpmDeps.awsSdk ::
      NpmDeps.aws4 ::
      Nil
  )

lazy val root = project
  .in(file("."))
  .settings(
    skip in publish := true,
  )
  .aggregate(api.js, api.jvm, eventData, eventPersistency, eventDistributor, webApi, lambdaApi, webClient)


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
