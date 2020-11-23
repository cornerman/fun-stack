package fun.web

import fun.api.Api

import zio.{IO, Has, ZEnv}
import zio.internal.Platform

package object client {
  type WebEnv =
    Has[Platform] with
    Has[Api_] with
    ZEnv

  type ApiResult[+R] = IO[ApiError, R]

  type Api_ = Api[ApiResult]
}
