package fun.web.client.aws

import cats.effect.IO

object Websocket {
  def open(url: Url): IO[Unit] = IO.unit
}
