package fun.event.distributor

import zio._

object Main extends App {
  def run(args: List[String]) =
    appLogic
      // .provideCustomLayer(appLayer)
      .exitCode

  // private[this] val appLayer =

  private[this] val appLogic =
    for {
      _ <- EventHandler.run
    } yield ()
}
