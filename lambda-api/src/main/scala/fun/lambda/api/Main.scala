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
  def handler(event: LambdaEvent, context: Context): js.Promise[LambdaResponse]  = {
    val handler = appLogic(event, context)
      .catchAll(e => ZIO.succeed(new LambdaResponse { error = e.toString }))
      .catchAllDefect(e => ZIO.succeed(new LambdaResponse { error = e.toString }))
      .provideCustomLayer(appLayer)
      .tap(response => putStrLn(js.JSON.stringify(response)))

    import scala.scalajs.js.JSConverters._
    import scala.concurrent.ExecutionContext.Implicits.global
    Runtime.default.unsafeRunToFuture(handler).toJSPromise
  }

  def appLogic(event: LambdaEvent, context: Context) = for {
    _ <- putStrLn(js.JSON.stringify(event))
    _ <- putStrLn(js.JSON.stringify(context))
    path <- IO.fromEither(event.path.toRight(ApiError.BadRequest))
    payload <- IO.fromEither(event.payload.toRight(ApiError.BadRequest))
    value <- Server.handle(path.toList, ByteBuffer.wrap(payload.toByteArray))
    valueArray = new Array[Byte](value.remaining)
    _ = value.get(valueArray)
  } yield new LambdaResponse { response = valueArray.toBase64 }

  val appLayer =
    ZLayer.succeed[Api_](ApiLive) ++
    ZLayer.fromEffect[Any, Nothing, Database](DatabaseLive.create)
}
