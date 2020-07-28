package fun.web.api

import fun.web.api.data._

import zio._
import zio.console._

trait Database {
  def getState: URIO[ZEnv, State]
}

object DatabaseLive extends Database {

  override final def getState = for {
    _ <- putStrLn("GetState")
  } yield State.initial
}
