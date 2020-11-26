import Options._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

inThisBuild(Seq(
  version := "0.1.0-SNAPSHOT",

  scalaVersion := "2.13.1"
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

  requireJsDomEnv in Test := true,
  scalaJSUseMainModuleInitializer := true,
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
  scalaJSLinkerConfig ~= { _.withESFeatures(_.withUseECMAScript2015(false)) },
)

lazy val localeSettings = Seq(
  zonesFilter := {(z: String) => false},
)

lazy val webSettings = Seq(
  version in webpack := "4.43.0",
  version in startWebpackDevServer := "3.11.0",
  webpackDevServerExtraArgs := Seq("--progress", "--color"),
  webpackDevServerPort := 12345,
  webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack.config.dev.js"),
  npmDevDependencies in Compile ++= NpmDeps.webpackDependencies,
  webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),
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

lazy val webClient = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, LocalesPlugin, TzdbPlugin)
  .dependsOn(api.js)
  .in(file("web-client"))
  .settings(commonSettings, jsSettings, localeSettings, webSettings)
  .settings(
    libraryDependencies ++=
      Deps.sloth.value ::
      Deps.zio.core.value ::
      Deps.boopickle.value ::
      Deps.outwatch.core.value ::
      Deps.outwatch.zio.value ::
      Nil
  )

lazy val root = project
  .in(file("."))
  .settings(
    skip in publish := true,
  )
  .aggregate(api.js, api.jvm, eventData, eventPersistency, eventDistributor, webApi, webClient)


addCommandAlias("dev", "devInit; devWatchAll; devDestroy") // watch all
addCommandAlias("devf", "devInit; devWatchClient; devDestroy") // only watch frontend
addCommandAlias("deva", "devInit; devWatchApi; devDestroy") // only watch backend
addCommandAlias("devInit", "webApi/reStart; webClient/fastOptJS::webpack; webClient/fastOptJS::startWebpackDevServer")
addCommandAlias("devWatchAll", "~; webApi/reStart; webClient/fastOptJS::webpack")
addCommandAlias("devWatchClient", "~webClient/fastOptJS::webpack")
addCommandAlias("devWatchApi", "~webApi/reStart")
addCommandAlias("devDestroy", "webClient/fastOptJS::stopWebpackDevServer; webApi/reStop")
