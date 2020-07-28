package fun.web.api

import fun.web.api.data._
import fun.api.Api

import zio.ZIO

object ApiLive extends Api[ApiResult] {
  def get: ApiResult[Int] = ZIO.accessM { env =>
    for {
      state <- env.get[Database].getState
    } yield state.value
  }

  def increment: ApiResult[Unit] = ZIO.accessM { env =>
    for {
      _ <- env.get[CommandHandler].send(Command.IncrementValue)
    } yield ()
  }
}
