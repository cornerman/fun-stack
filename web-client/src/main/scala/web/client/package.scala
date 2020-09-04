package fun.web

import fun.api.Api

import zio.IO

package object client {
  type ApiResult[+R] = IO[ApiError, R]

  type Api_ = Api[ApiResult]
}
