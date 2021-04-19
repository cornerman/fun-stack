package fun.web.client

import fun.web.client.aws.Fun
import fun.api.Api

import cats.effect.IO

import sloth._

import java.nio.ByteBuffer
import boopickle.Default._
import chameleon.ext.boopickle._

object WsClient {
  private val client = Client[ByteBuffer, IO, ClientException](Fun.ws.transport[String, String, ByteBuffer])

  val api = client.wire[Api[IO]]
}
