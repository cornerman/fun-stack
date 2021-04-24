package fun.web.client

import fun.web.client.aws.Fun
import fun.api.Api

import cats.implicits._
import cats.effect.IO

import sloth._

import java.nio.ByteBuffer
import boopickle.Default._
import chameleon.{Deserializer, Serializer}
import chameleon.ext.boopickle._

object WsClient {

  import fun.web.client.aws.Base64Serdes._
  private val client = Fun.api.wsClient[ByteBuffer]

  val api = client.wire[Api[IO]]
}
