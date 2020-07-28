package fun.web.client

import zio._

object Main extends App {

  def run(args: List[String]) = {
    println("hallo")
    appLogic
      // .provideCustomLayer(appLayer)
      .exitCode
  }

  // private[this] val appLayer =

  private[this] val appLogic = ZIO.succeed(())
}
