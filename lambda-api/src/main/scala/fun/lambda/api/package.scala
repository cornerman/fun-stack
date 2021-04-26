package fun.lambda

import fun.api.Api

import zio.{ZEnv, ZIO, Has}

package object api {
  type ApiEnv =
    Has[Database] with ZEnv

  type ApiResult[+R] = ZIO[ApiEnv, ApiError, R]

  type Api_ = Api[ApiResult]

  // type PickleType = java.nio.ByteBuffer
}
