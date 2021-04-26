package fun.lambda.api

import fun.api.Api

import net.exoego.facade.aws_lambda._
import zio._
import fun.lambda.api.aws._
import java.nio.ByteBuffer
import boopickle.Default._
import chameleon.ext.boopickle._
import fun.lambda.api.aws.Base64Serdes._
import zio.interop.catz._

import scala.scalajs.js

object Main {

  @js.annotation.JSExportTopLevel("handler")
  def handler(event: APIGatewayWSEvent, context: Context): js.Promise[APIGatewayProxyStructuredResultV2] = {
    import scala.scalajs.js.JSConverters._
    import scala.concurrent.ExecutionContext.Implicits.global
    Handler.handle[ByteBuffer, String, String, ApiResult](
      _.route[Api[ApiResult]](ApiLive),
      event,
      x => Runtime.default.unsafeRunToFuture(x.mapError(_.toString).either.provideCustomLayer(cachedAppLayer)).toJSPromise,
    )
  }

  val appLayer =
    ZLayer.fromEffect[Any, Nothing, Database](DatabaseLive.create)

  val cachedAppLayer = Runtime.default.unsafeRunTask(appLayer.memoize.useNow)
}
