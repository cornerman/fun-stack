package fun.web.client

import outwatch._
import outwatch.dsl._

import cats.effect.SyncIO

object Main {

  def main(args: Array[String]): Unit = {
    val app = Outwatch.renderInto[SyncIO]("#app", div("ho"))

    app.unsafeRunSync()
  }
}
