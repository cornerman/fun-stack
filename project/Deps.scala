import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

object Deps {
  import Def.{ setting => dep }

  // testing
  val scalatest = dep("org.scalatest" %%% "scalatest" % "3.2.0")

  // core libraries
  val cats = new {
    val core = dep("org.typelevel" %%% "cats-core" % "2.1.1")
  }
  val zio = new {
    val core = dep("dev.zio" %%% "zio" % "1.0.3")
    val cats = dep("dev.zio" %%% "zio-interop-cats" % "2.1.4.0")
  }

  // serialization
  val boopickle = dep("io.suzaku" %%% "boopickle" % "1.3.2")

  // rpc
  val sloth = dep("com.github.cornerman" %%% "sloth" % "0.3.0")

  // web server
  val http4s = new {
    private val version = "1.0.0-M3"
    val server = dep("org.http4s" %% "http4s-blaze-server" % version)
    val dsl = dep("org.http4s" %% "http4s-dsl" % version)
  }

  // web app
  val outwatch = new {
    private val version = "d9b5d516"
    val core = dep("com.github.cornerman.outwatch" %%% "outwatch" % version)
    val zio = dep("com.github.cornerman.outwatch" %%% "outwatch-zio" % version)
  }

  // utils
  val jsrequests = dep("com.github.cornerman.simple-scalajs-requests" %%% "requests" % "c35e198")
  val cuid = dep("io.github.cornerman.scala-cuid" %%% "scala-cuid" % "f1f7638")
}

object NpmDeps {
  val webpackDependencies =
    "copy-webpack-plugin" -> "5.0.0" ::
    "clean-webpack-plugin" -> "1.0.1" ::
    Nil
}
