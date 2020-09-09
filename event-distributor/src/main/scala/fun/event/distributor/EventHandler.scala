package fun.event.distributor

import fun.event.data.Event

import zio._
import zio.console._

object EventHandler {
  def run: URIO[ZEnv, Unit] =
    for {
      _ <- putStrLn("EventHandler started")
      _ <- onEvent(Event.IncrementValue)
    } yield ()

  private def onEvent(event: Event) = event match {
    case Event.IncrementValue => ZIO.succeed(())
  }
}
