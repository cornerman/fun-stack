package fun.web.client

import zio._
import zio.interop.catz._

import outwatch._

object Web {
  val render = ZIO.accessM[WebEnv] { env =>
    OutWatch.renderInto[Task, WebEnv]("#app", Component.root, env)
  }
}

object Component {
  import outwatch.dsl._

  //TODO: get rid of runtime? or use map...we need proper support for zio in colibri and outwatch?
  implicit def r = zio.Runtime.default

  val getState: RModifier[WebEnv] = div(
    button(
      "Get State",
      Modifier.access[Has[Api_]] { env =>
        onClick.useAsync(env.get[Api_].getState.either: Task[Either[ApiError, Int]]).foreach(s => println("GOT " + s))
      }
    )
  )

  val root = div("hello world", getState)
}
