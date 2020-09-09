package fun.web.api

import fun.web.api.data.Command
import fun.event.data.Event

import zio._
import zio.console._

trait CommandHandler {
  def send(cmd: Command): URIO[ZEnv, Boolean]
}

object CommandHandlerLive extends CommandHandler {

  override final def send(cmd: Command) = for {
    _ <- putStrLn(s"Handle command: $cmd")
    event = commandToEvent(cmd)
    _ <- putStrLn(s"Transformed to event: $event")
    success <- sendEvent(event)
  } yield success

  private val commandToEvent: Command => Event = {
    case Command.IncrementValue => Event.IncrementValue
  }

  private def sendEvent(event: Event) = {
    ZIO.succeed(true)
  }
}
