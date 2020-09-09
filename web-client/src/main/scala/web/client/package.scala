package fun.web

import fun.api.Api

import zio.{IO, Has, ZEnv}

package object client {
  type WebEnv =
    Has[Api_] with
    ZEnv

  type ApiResult[+R] = IO[ApiError, R]

  type Api_ = Api[ApiResult]
}
