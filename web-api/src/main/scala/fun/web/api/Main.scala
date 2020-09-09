package fun.web.api

import fun.api.Api

import zio._

object Main extends App {
  def run(args: List[String]) =
    appLogic
      .provideCustomLayer(appLayer)
      .exitCode

  private val appLayer =
    ZLayer.succeed(ServerConfig.default) ++
    ZLayer.succeed[CommandHandler](CommandHandlerLive) ++
    ZLayer.succeed[Database](DatabaseLive) ++
    ZLayer.succeed[Api_](ApiLive)

  private val appLogic =
    for {
      _ <- Server.run
    } yield ()
}
