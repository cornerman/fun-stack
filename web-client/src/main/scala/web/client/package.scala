package fun.web

import fun.api.Api

import colibri.Observable
import outwatch.EventDispatcher

import zio.{IO, Has, ZEnv}
import zio.internal.Platform

package object client {
  type Config = ConfigF[Observable]

  type WebEnv =
    Has[Platform] with
    Has[Api_] with
    Has[Config] with
    Has[EventDispatcher[Event]] with
    ZEnv

  type ApiResult[+R] = IO[ApiError, R]

  type Api_ = Api[ApiResult]
}
