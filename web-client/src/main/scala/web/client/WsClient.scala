package fun.web.client

import funstack.web.Fun
import fun.api.Api

import cats.implicits._
import cats.effect.IO

import sloth._

import java.nio.ByteBuffer
import boopickle.Default._
import chameleon.{Deserializer, Serializer}
import chameleon.ext.boopickle._
import funstack.core.Base64Serdes._

object WsClient {

  private val client = Fun.api.map(_.wsClient[ByteBuffer, IO])

  val api = client.map(_.wire[Api[IO]])
}
