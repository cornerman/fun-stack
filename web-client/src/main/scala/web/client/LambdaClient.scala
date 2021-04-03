package fun.web.client

import requests.JsRequestBytes
import fun.api.Api

import zio._
import zio.interop.catz.core._

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
  private val config = AWSConfig()
  private val lambda = new Lambda(config)
  private val lambdaName = ""

  def apply(request: Request[ByteBuffer]): ApiResult[ByteBuffer] = {
    val payload = js.Dynamic.literal(path = request.path.toJSArray, payload = request.payload.array.toBase64)
    val invocation = InvocationRequest(
      FunctionName = lambdaName,
      InvocationType = InvocationType.RequestResponse,
      Payload = js.JSON.stringify(payload)
    )
    ZIO.fromFuture(_ => lambda.invoke(invocation).promise().toFuture)
      .map(r => ByteBuffer.wrap(r.Payload.asInstanceOf[String].toByteArray))
      .mapError(ApiError.RequestFailed(_))
  }
}
