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

  val getState: ModifierM[WebEnv] = div(
    input(
      value.useAccess[WebEnv] <-- ZIO.succeed((s:WebEnv) => s.toString)
    ),
    button(
      "Get State",
      onClick.useAccess[WebEnv].mapZIOSingleOrDrop(_.get[Api_].getState.either).foreach(s => println("GOT " + s)),
      onClick.useZIOSingleOrDrop(RIO.accessM[WebEnv](_.get[Api_].getState.either)).foreach(s => println("GOT " + s))
    )
  )

  val root = div("hello world", getState)
}
