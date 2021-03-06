package fun.web

import zio.{ZEnv, ZIO, Has}

package object api {
  type ApiEnv =
    Has[Database] with
    Has[CommandHandler] with
    ZEnv

  type ApiResult[+R] = ZIO[ApiEnv, ApiError, R]
}
