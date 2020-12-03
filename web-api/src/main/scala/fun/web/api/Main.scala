package fun.web.api

import fun.api.Api

import zio._

object Main extends App {
  def run(args: List[String]) =
    appLogic
      .provideCustomLayer(appLayer)
      .exitCode

  private val appLayer =
    ZLayer.succeed[ServerConfig](ServerConfig.default) ++
    ZLayer.succeed[Api_](ApiLive) ++
    ZLayer.fromEffect[Any, Nothing, Database](DatabaseLive.create)

  private val appLogic =
    for {
      _ <- Server.run
    } yield ()
}
