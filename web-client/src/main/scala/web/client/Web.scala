package fun.web.client

import zio.Task
import zio.interop.catz._

import outwatch.OutWatch
import outwatch.dsl._

object Web {
  val root = div("hello world")

  val render = OutWatch.renderInto[Task]("#app", root)
}
