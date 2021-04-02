import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

object Deps {
  import Def.{ setting => dep }

  // testing
  val scalatest = dep("org.scalatest" %%% "scalatest" % "3.2.0")

  // core libraries
  val cats = new {
    val core = dep("org.typelevel" %%% "cats-core" % "2.1.1")
    val effect = dep("org.typelevel" %%% "cats-effect" % "2.3.0")
  }
  val zio = new {
    val core = dep("dev.zio" %%% "zio" % "1.0.1")
    val cats = dep("dev.zio" %%% "zio-interop-cats" % "2.3.1.0")
  }

  // serialization
  val boopickle = dep("io.suzaku" %%% "boopickle" % "1.3.2")

  // rpc
  val sloth = dep("com.github.cornerman.sloth" %%% "sloth" % "c0c6ef0")

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
  val base64 = dep("com.github.marklister" %%% "base64" % "0.3.0")

  // aws-sdk-js
  val awsSdkJS = dep("net.exoego" %%% "aws-sdk-scalajs-facade" % "0.32.0-v2.798.0")
  val awsLambdaJS = dep("net.exoego" %%% "aws-lambda-scalajs-facade" % "0.11.0")
}

object NpmDeps {
  val awsSdk = "aws-sdk" -> "2.798.0"

  val webpackDependencies =
    "copy-webpack-plugin" -> "5.0.0" ::
    "clean-webpack-plugin" -> "1.0.1" ::
    Nil
}
