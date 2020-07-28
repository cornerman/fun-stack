import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

object Deps {
  import Def.{ setting => dep }

  val silencerVersion = "1.4.2"

  // testing
  val scalatest = dep("org.scalatest" %%% "scalatest" % "3.2.0")

  // core libraries
  val cats = new {
    val core = dep("org.typelevel" %%% "cats-core" % "2.1.1")
  }
  val zio = new {
    val core = dep("dev.zio" %%% "zio" % "1.0.0")
    val cats = dep("dev.zio" %%% "zio-interop-cats" % "2.1.4.0-RC17")
  }

  // serialization
  val boopickle = dep("io.suzaku" %%% "boopickle" % "1.3.2")

  // rpc
  val sloth = dep("com.github.cornerman" %%% "sloth" % "0.3.0")
  val http4s = new {
    private[this] val version = "1.0.0-M3"
    val server = dep("org.http4s" %% "http4s-blaze-server" % version)
    val dsl = dep("org.http4s" %% "http4s-dsl" % version)
  }

  // webApp
  val outwatch = new {
    private[this] val version = "61deece"
    val core = dep("com.github.outwatch.outwatch" %%% "outwatch" % version)
  }
}
