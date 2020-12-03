package fun.web.client

import fun.api.Command

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
  val incrementState = ZIO.accessM[WebEnv](_.get[Api_].sendCommand(Command.IncrementValue))

  val getState: ModifierM[WebEnv] = div(

    button(
      "Increment",
      onClick.useZIOSingleOrDrop(incrementState.either).discard,
    ),

    button(
      "Get State",
      onClick.useZIOSingleOrDrop(requestState.either).handled { stream =>
        stream.map {
          case Right(state) => state.toString
          case Left(error) => error.toString
        }
      }
    )

  )

  val root = div("hello world", getState)
}
