package fun.web.client

import zio._
import zio.console._
import zio.internal.Platform

object Main extends App {

  def run(args: List[String]) = {
    appLogic
      .provideCustomLayer(appLayer)
      .exitCode
  }

  private val appLayer =
    ZLayer.succeed[Api_](HttpClient.api) ++
    ZLayer.succeed[Platform](Platform.default)

  private val appLogic = for {
    _ <- Web.render
  } yield ()
}
