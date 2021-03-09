package fun.web

import fun.api.Api
import fun.web.client.data._

import colibri.Observable
import outwatch.{ModifierM, EventDispatcher}

import zio._
import zio.internal.Platform

package object client {

  type WebEnv =
    Has[Platform] with
    Has[Api_] with
    Has[Config] with
    Has[EventDispatcher[Event]] with
    ZEnv

  object WebEnv {
    @inline def get[T : Tag](f: T => ModifierM[WebEnv])(implicit e: WebEnv <:< Has[T]): ModifierM[WebEnv] = ModifierM.accessM[WebEnv](env => f(env.get[T]))
  }

  type ApiResult[+R] = IO[ApiError, R]

  type Api_ = Api[ApiResult]
}
