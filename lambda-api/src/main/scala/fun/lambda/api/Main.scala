package fun.lambda.api

import fun.api.Api

import net.exoego.facade.aws_lambda._
import zio._
import zio.console._
import com.github.marklister.base64.Base64._
import cats.Functor
import cats.implicits._

import scala.scalajs.js
import mycelium.core.message._

object Helper {
  import chameleon.{Serializer, Deserializer}
  import sloth._

  def handle[T, Event](router: Router[T, ApiResult])(
      event: APIGatewayWSEvent,
  )(implicit deserializer: Deserializer[ClientMessage[T], String], serializer: Serializer[ServerMessage[T, Event, ApiError], String]) = {
    val result = Deserializer[ClientMessage[T], String].deserialize(event.body) match {
      case Left(error) => ZIO.fail(new Exception(s"Deserializer: $error"))
      case Right(Ping) => ZIO.succeed(Pong)
      case Right(CallRequest(seqNumber, path, payload)) =>
        router(Request(path, payload)) match {
          case RouterResult.Success(_, result) => result.map(_.serialized).either.map(CallResponse(seqNumber, _))
          case RouterResult.Failure(_, result) => ZIO.succeed(CallResponse(seqNumber, Left(ApiError.ServerFailure(result.toString))))
        }
    }

    result
      .map(Serializer[ServerMessage[T, Event, ApiError], String].serialize)
      .map(payload => APIGatewayProxyStructuredResultV2(body = payload, statusCode = 200))
      .catchAllDefect(e => ZIO.succeed(APIGatewayProxyStructuredResultV2(body = e.toString, statusCode = 500)))
  }
}

object Main {
  import java.nio.ByteBuffer
  import boopickle._
  import boopickle.Default._
  import chameleon.ext.boopickle._
  import fun.lambda.api.aws.Base64Serdes._
  import Helper._
  import zio.interop.catz._
  import zio.interop.catz.implicits._

  @js.annotation.JSExportTopLevel("handler")
  def handler(event: APIGatewayWSEvent, context: Context): js.Promise[APIGatewayProxyStructuredResultV2] = {
    import scala.scalajs.js.JSConverters._
    import scala.concurrent.ExecutionContext.Implicits.global
    Runtime.default.unsafeRunToFuture(appLogic(event, context)).toJSPromise
  }

  def appLogic(event: APIGatewayWSEvent, context: Context) =
    handle[ByteBuffer, String](sloth.Router[ByteBuffer, ApiResult].route(ApiLive))(event)
      .provideCustomLayer(cachedAppLayer)
      .tap(response => putStrLn(js.JSON.stringify(response)))

  val appLayer =
    ZLayer.fromEffect[Any, Nothing, Database](DatabaseLive.create)

  val cachedAppLayer = Runtime.default.unsafeRunTask(appLayer.memoize.useNow)
}
