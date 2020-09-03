package fun.web.client

import zio._
import zio.interop.catz._

import outwatch.OutWatch

object Main extends App {

  def run(args: List[String]) = {
    appLogic
      // .provideCustomLayer(appLayer)
      .exitCode
  }

  // private val appLayer =

  private val appLogic = for {
    _ <- OutWatch.renderInto[Task]("#app", Web.root)
  } yield ()
}
