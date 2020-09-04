package fun.web.client

import fun.web.client.helpers.JsRequest
import fun.api.Api

import zio._
import zio.interop.catz.core._

import sloth._

import java.nio.ByteBuffer
import boopickle.Default._
import chameleon.ext.boopickle._

import org.scalajs.dom

object HttpClient {
  private val client = Client[ByteBuffer, ApiResult, ApiError](HttpTransport)

  val api = client.wire[Api[ApiResult]]
}

private object HttpTransport extends RequestTransport[ByteBuffer, ApiResult] {

  private val baseUrl = {
    val loc = dom.window.location
    val backendPort = 8080
    s"${loc.protocol}//${loc.hostname}:${backendPort}"
  }

  def apply(request: Request[ByteBuffer]): ApiResult[ByteBuffer] = {
    println(request)
    println(baseUrl)
    val url = baseUrl + "/" + request.path.mkString("/")
    val payload = request.payload

    ZIO.fromFuture(implicit ec => JsRequest.post(url, payload))
      .mapError(ApiError.RequestFailed(_))
  }
}
