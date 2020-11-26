package fun.web.client

import zio._
import zio.interop.catz._

import outwatch._
import outwatch.z._

object Web {
  val render = ZIO.accessM[WebEnv] { env =>
    Outwatch.renderInto[Task]("#app", Component.root.provide(env))
  }
}

object Component {
  import outwatch.dsl._

  val requestState = ZIO.accessM[WebEnv](_.get[Api_].getState)

  val getState: ModifierM[WebEnv] = div(
    button(
      "Get States",
      onClick.useZIOSingleOrDrop(requestState.either).foreach(s => println("GOT " + s)),
    )
  )

  val root = div("hello world", getState)
}
