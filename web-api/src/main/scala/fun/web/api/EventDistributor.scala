package fun.web.api

import fun.event.data.Event

import zio._
import zio.console._

trait EventDistributor {
  def send(event: Event): URIO[ZEnv, Unit]
}

object EventDistributorLive extends EventDistributor {
  def send(event: Event) = for {
    _ <- putStrLn(s"Distribute event: $event")
  } yield ()
}
