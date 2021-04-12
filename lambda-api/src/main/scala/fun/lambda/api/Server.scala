package fun.lambda.api

import fun.api.Api

import zio._
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.console._

import java.nio.ByteBuffer
import scala.concurrent.duration._

import sloth._
import chameleon.ext.boopickle._

import facade.amazonaws.services.lambda.Lambda
import scalajs.js
import scalajs.js.annotation.JSExportTopLevel
import scala.concurrent.{Future, ExecutionContext}

object Server {

  def handle(path: List[String], payload: PickleType): ZIO[
    Has[Api[ApiResult]] with ApiEnv,
    ApiError,
    PickleType
  ] = ZIO.accessM { env =>
    import boopickle.Default._

    val apiImpl = env.get[Api[ApiResult]]
    val router  = Router[ByteBuffer, ApiResult].route(apiImpl)

    router(Request(path, payload)) match {
      case RouterResult.Success(_, result) => result.map(_.serialized)
      case RouterResult.Failure(_, result) => ZIO.fail(ApiError.ServerFailure(result.toString))
    }
  }
}
