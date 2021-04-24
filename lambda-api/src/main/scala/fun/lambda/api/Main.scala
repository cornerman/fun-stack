package fun.lambda.api

import fun.api.Api

import net.exoego.facade.aws_lambda._
import zio._
import zio.console._
import com.github.marklister.base64.Base64._

import scala.scalajs.js
import mycelium.core.message._

object Helper {
  import chameleon.Serializer

  def toResult[T: Serializer[?, String]](message: T) = {
    val serialized = Serializer[T, String].serialize(message)
    APIGatewayProxyStructuredResultV2(body = serialized, statusCode = 200)
  }
}
import Helper._
import aws.Base64Serdes._

object Main {
  import java.nio.ByteBuffer
  import boopickle._
  import boopickle.Default._

  @js.annotation.JSExportTopLevel("handler")
  def handler(event: APIGatewayWSEvent, context: Context): js.Promise[APIGatewayProxyStructuredResultV2] = {
    val handler = appLogic(event, context)
      .catchAllDefect(e => ZIO.succeed(APIGatewayProxyStructuredResultV2(body = e.toString, statusCode = 500)))
      .provideCustomLayer(cachedAppLayer)
      .tap(response => putStrLn(js.JSON.stringify(response)))

    import scala.scalajs.js.JSConverters._
    import scala.concurrent.ExecutionContext.Implicits.global
    Runtime.default.unsafeRunToFuture(handler).toJSPromise
  }

  def deserializeEventPayload(event: APIGatewayWSEvent) = for {
    byteArray <- Task(event.body.toByteArray)
    message   <- Task(Unpickle[ClientMessage[ByteBuffer]].fromBytes(ByteBuffer.wrap(byteArray)))
  } yield message

  def appLogic(event: APIGatewayWSEvent, context: Context) = for {
    message  <- deserializeEventPayload(event)
    response <- onMessage(message)
  } yield toResult(response)

  def onMessage(message: ClientMessage[ByteBuffer]) = message match {
    case Ping => ZIO.succeed(Pong)
    case CallRequest(seqNumber, path, payload) =>
      for {
        response <- Server.handle(path.toList, payload).either
      } yield CallResponse(seqNumber, response)
  }

  def byteBufferToArray(bytes: ByteBuffer): Array[Byte] =
    if (bytes.hasArray) bytes.array
    else {
      val array = new Array[Byte](bytes.remaining)
      bytes.rewind()
      bytes.get(array)
      array
    }

  val appLayer =
    ZLayer.succeed[Api_](ApiLive) ++
      ZLayer.fromEffect[Any, Nothing, Database](DatabaseLive.create)

  val cachedAppLayer = Runtime.default.unsafeRunTask(appLayer.memoize.useNow)
}
