package fun.lambda.api

import fun.api.{Api, Command}

import zio.ZIO

object ApiLive extends Api[ApiResult] {
  def getState: ApiResult[Int] = ZIO.accessM { env =>
    env.get[Database].getState.map(_.value)
  }

  def sendCommand(command: Command): ApiResult[Unit] = ZIO.accessM { env =>
    val db = env.get[Database]
    command match {
      case Command.IncrementValue => db.updateState(state => state.copy(value = state.value + 1))
      case Command.DecrementValue => db.updateState(state => state.copy(value = state.value - 1))
      case Command.SetValue(value) => db.setState(State(value))
    }
  }
}
