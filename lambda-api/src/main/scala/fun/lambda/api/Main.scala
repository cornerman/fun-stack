package fun.lambda.api

import fun.api.Api

import zio._
import zio.interop.catz.core._
import funstack.lambda.ws.Handler
import java.nio.ByteBuffer
import scala.scalajs.js

import boopickle.Default._
import chameleon.ext.boopickle._
import funstack.core.Base64Serdes._

object Main {

  val router = sloth.Router[ByteBuffer, ApiResult].route[Api[ApiResult]](ApiLive)

  @js.annotation.JSExportTopLevel("handler")
  val handler = Handler.handle[ByteBuffer, String, String, ApiResult](
    router,
    x => Runtime.default.unsafeRunToFuture(x.mapError(_.toString).either.provideCustomLayer(cachedAppLayer)),
  )

  val appLayer =
    ZLayer.fromEffect[Any, Nothing, Database](DatabaseLive.create)

  val cachedAppLayer = Runtime.default.unsafeRunTask(appLayer.memoize.useNow)
}
