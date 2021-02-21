package fun.web.api

import fun.api.{Api, Command}

import zio.ZIO

object ApiLive extends Api[ApiResult] {
  def getState: ApiResult[Int] = ZIO.accessM { env =>
    env.get[Database].getState.map(_.value)
  }

  def sendCommand(command: Command): ApiResult[Unit] = ZIO.accessM { env =>
    val modifier = StateModifier.modifier(command)
    env.get[Database].updateState(modifier)
  }
}
