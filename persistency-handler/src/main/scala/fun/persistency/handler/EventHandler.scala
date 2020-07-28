package fun.persistency.handler

import fun.persistency.events.Event

import zio._
import zio.console._

object EventHandler {
  def run: URIO[ZEnv, Unit] =
    for {
      _ <- putStrLn("EventHandler started")
      _ <- onEvent(Event.IncrementValue)
    } yield ()

  private[this] def onEvent(event: Event) = event match {
    case Event.IncrementValue => ZIO.succeed(())
  }
}
