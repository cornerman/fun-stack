import Options._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

inThisBuild(Seq(
  version := "0.1.0-SNAPSHOT",

  crossScalaVersions := Seq("2.12.10", "2.13.1"),

  scalaVersion := crossScalaVersions.value.last,
))

lazy val commonSettings = Seq(
  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full),

  resolvers ++=
    ("jitpack" at "https://jitpack.io") ::
    Nil,

  libraryDependencies ++= Seq(
    "org.scalatest" %%% "scalatest" % "3.2.0" % Test,
  ),

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
  scalaJSLinkerConfig ~= { _.withESFeatures(_.withUseECMAScript2015(false)) }
)

lazy val api = project
  .in(file("api"))
  .settings(commonSettings)

lazy val persistencyEvents = project
  .in(file("persistency-events"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      Deps.zio.core.value,
    )
  )

lazy val persistencyHandler = project
  .dependsOn(persistencyEvents)
  .in(file("persistency-handler"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      Deps.zio.core.value,
    )
  )

lazy val webApi = project
  .dependsOn(api, persistencyEvents)
  .in(file("web-api"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      Deps.sloth.value,
      Deps.zio.core.value,
      Deps.zio.cats.value,
      Deps.http4s.server.value,
      Deps.http4s.dsl.value,
      Deps.boopickle.value,
    )
  )

lazy val webClient = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(api)
  .in(file("web-client"))
  .settings(commonSettings, jsSettings)
  .settings(
    version in webpack := "4.43.0",
    version in startWebpackDevServer := "3.11.0",
    webpackDevServerExtraArgs := Seq("--progress", "--color"),
    webpackDevServerPort := 12345,
    webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack.config.dev.js"),

    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % "2.0.0",

    libraryDependencies ++= Seq(
      Deps.zio.core.value,
      Deps.zio.cats.value,
      Deps.sloth.value,
      Deps.boopickle.value,
      Deps.outwatch.core.value,
    )
  )

lazy val root = project
  .in(file("."))
  .settings(
    skip in publish := true,
  )
  .aggregate(api, persistencyEvents, persistencyHandler, webApi, webClient)


// hot reloading configuration:
// https://github.com/scalacenter/scalajs-bundler/issues/180
addCommandAlias("dev", "; webClient/compile; webClient/fastOptJS::startWebpackDevServer; devwatch; webClient/fastOptJS::stopWebpackDevServer")
addCommandAlias("devwatch", "~; webClient/fastOptJS; copyFastOptJS")

// when running the "dev" alias, after every fastOptJS compile all artifacts are copied into
// a folder which is served and watched by the webpack devserver.
// this is a workaround for: https://github.com/scalacenter/scalajs-bundler/issues/180
lazy val copyFastOptJS = TaskKey[Unit]("copyFastOptJS", "Copy javascript files to target directory")
copyFastOptJS := {
  val inDir = (crossTarget in (Compile, fastOptJS)).value
  val outDir = (crossTarget in (Compile, fastOptJS)).value / "dev"
  val files = Seq(name.value.toLowerCase + "-fastopt-loader.js", name.value.toLowerCase + "-fastopt.js") map { p => (inDir / p, outDir / p) }
  IO.copy(files, overwrite = true, preserveLastModified = true, preserveExecutable = true)
}
