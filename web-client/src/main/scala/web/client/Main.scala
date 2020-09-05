package fun.web.client

import zio._
import zio.console._

object Main extends App {

  def run(args: List[String]) = {
    appLogic
      .provideCustomLayer(appLayer)
      .exitCode
  }

  private val appLayer =
    ZLayer.succeed[Api_](HttpClient.api)

  private val appLogic = for {
    _ <- Web.render
    state <- ZIO.accessM[Has[Api_]](env => env.get[Api_].getState.mapError(err => new Exception(err.toString)))
    _ <- putStrLn("DONE"  + state)
  } yield ()
}
