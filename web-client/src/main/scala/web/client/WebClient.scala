package fun.web.client

import fun.api.Api

import zio._
import zio.interop.catz.core._

import sloth._

import java.nio.ByteBuffer
import boopickle.Default._
import chameleon.ext.boopickle._

import scala.scalajs.js
import scala.scalajs.js.typedarray._
import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import org.scalajs.dom.experimental.{Fetch, RequestInit, HttpMethod, BodyInit, ReadableStreamReader}

object WebClient {
  private val client = Client[ByteBuffer, ApiResult, ApiError](WebTransport)

  val api = client.wire[Api[ApiResult]]
}

private object WebTransport extends RequestTransport[ByteBuffer, ApiResult] {
  private val baseUrl = "http://localhost:8080"

  def apply(request: Request[ByteBuffer]): ApiResult[ByteBuffer] =
    makeRequest(request).mapError(e => ApiError.RequestFailed(e))

  private def makeRequest(request: Request[ByteBuffer]): Task[ByteBuffer] =
    for {
      result <- fetchData(request)
      buffers <- readBytes(result.body.getReader)
    } yield combineByteBuffers(buffers)

  private def fetchData(request: Request[ByteBuffer]) =
    ZIO.fromPromiseJS(Fetch.fetch(
      baseUrl + "/" + request.path.mkString("/"),
      js.Dynamic.literal(method = HttpMethod.POST, body = request.payload.typedArray).asInstanceOf[RequestInit]
    ))

  private def readBytes(reader: ReadableStreamReader[Uint8Array]): Task[List[ByteBuffer]] =
    ZIO.fromPromiseJS(reader.read()).flatMap { chunk =>
      val buffer = TypedArrayBuffer.wrap(chunk.value.buffer)
      if (chunk.done) ZIO.succeed(buffer :: Nil)
      else readBytes(reader).map(buffer :: _)
    }

  private def combineByteBuffers(buffers: List[ByteBuffer]): ByteBuffer = {
    val result = ByteBuffer.allocate(buffers.map(_.limit()).sum);
    buffers.foreach(result.put)
    result
  }
}
