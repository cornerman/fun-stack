package fun.lambda.api

import fun.api.Api

import net.exoego.facade.aws_lambda._
import zio._
import zio.console._
import com.github.marklister.base64.Base64._

import scala.scalajs.js
import java.nio.ByteBuffer

object Main {

  @js.annotation.JSExportTopLevel("handler")
  def handler(event: APIGatewayWSEvent, context: Context): js.Promise[APIGatewayProxyStructuredResultV2] = {
    val handler = appLogic(event, context)
      // .catchAll(e => ZIO.succeed(new APIGatewayWSResponse { error = e.toString }))
      .catchAllDefect(e => ZIO.succeed(APIGatewayProxyStructuredResultV2(body = e.toString, statusCode = 500)))
      .provideCustomLayer(cachedAppLayer)
      .tap(response => putStrLn(js.JSON.stringify(response)))

    import scala.scalajs.js.JSConverters._
    import scala.concurrent.ExecutionContext.Implicits.global
    Runtime.default.unsafeRunToFuture(handler).toJSPromise
  }

  def appLogic(event: APIGatewayWSEvent, context: Context) = for {
    _ <- putStrLn(js.JSON.stringify(event))
    _ <- putStrLn(js.JSON.stringify(context))
    // path    <- IO.fromEither(event.path.toRight(ApiError.BadRequest))
    // payload <- IO.fromEither(event.payload.toRight(ApiError.BadRequest))
    // value   <- Server.handle(path.toList, ByteBuffer.wrap(payload.toByteArray))
    // valueArray = new Array[Byte](value.remaining)
    // _          = value.get(valueArray)
    // } yield new APIGatewayWSResponse { response = valueArray.toBase64 }
  } yield APIGatewayProxyStructuredResultV2(body = "OK", statusCode = 200)

  val appLayer =
    ZLayer.succeed[Api_](ApiLive) ++
      ZLayer.fromEffect[Any, Nothing, Database](DatabaseLive.create)

  val cachedAppLayer = Runtime.default.unsafeRunTask(appLayer.memoize.useNow)
}
