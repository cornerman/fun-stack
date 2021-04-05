package fun.web.client

import requests.JsRequestBytes
import fun.api.Api

import zio._
import zio.interop.catz._

import sloth._

import java.nio.ByteBuffer
import boopickle.Default._
import chameleon.ext.boopickle._
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

import facade.amazonaws.AWSConfig
import facade.amazonaws.services.lambda._

import com.github.marklister.base64.Base64._

import org.scalajs.dom

object LambdaClient {
  private val client = Client[ByteBuffer, ApiResult, ApiError](LambdaTransport)

  val api = client.wire[Api[ApiResult]]
}

object LambdaTransport extends RequestTransport[ByteBuffer, ApiResult] {
  private val lambdaName = "lambda-api"

  def apply(request: Request[ByteBuffer]): ApiResult[ByteBuffer] = ZIO.accessM[ApiEnv] { env =>
    //TODO headIO
    UIO.effectAsyncInterrupt[aws.User] { cb =>
      val cancelable = colibri.Cancelable.variable()
      cancelable() = env.get[aws.Auth].currentUser.foreach(_.foreach { user =>
        cancelable.cancel()
        cb(UIO.succeed(user))
      })
      Left(UIO(cancelable.cancel()))
    }.flatMap { user =>
      Task.effectSuspend {
        val config = AWSConfig(region = AppConfig.region, credentials = user.credentials)
        val lambda = new Lambda(config)

        println("PAY " + request.payload)
        val payloadArray = new Array[Byte](request.payload.remaining)
        request.payload.get(payloadArray)
        println("PAYA " + payloadArray)
        val payload = js.Dynamic.literal(path = request.path.toJSArray, payload = payloadArray.toBase64)

        val invocation = InvocationRequest(
          FunctionName = lambdaName,
          InvocationType = InvocationType.RequestResponse,
          Payload = js.JSON.stringify(payload)
        )

        Task.fromFuture(_ => lambda.invokeFuture(invocation))
          .map(r => ByteBuffer.wrap(js.JSON.parse(r.Payload.asInstanceOf[String]).asInstanceOf[js.Dynamic].response.asInstanceOf[String].toByteArray))
      }
    }.mapError(ApiError.RequestFailed(_))
  }
}
