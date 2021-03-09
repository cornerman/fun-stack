package fun.web.client.data

import cats.effect.SyncIO

import scala.scalajs.js

case class Timestamp(utcMillis: Long)
object Timestamp {
  def now = for {
    utcMillis <- SyncIO(new js.Date().getTime())
  } yield Timestamp(utcMillis.toLong)
}
