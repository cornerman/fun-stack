package fun.web

import zio.IO

package object client {
  type ApiResult[+R] = IO[ApiError, R]
}
