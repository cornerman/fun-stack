package fun.web

import fun.api.Api
import fun.web.client.data._
import fun.web.client.aws.Auth

import colibri.Observable
import outwatch.{ModifierM, EventDispatcher}

import zio._
import zio.internal.Platform

package object client {

  type WebEnv =
    Has[Platform] with
    Has[Api_] with
    Has[Config] with
    Has[Auth] with
    Has[EventDispatcher[Event]] with
    ZEnv

  type ApiEnv =
    Has[Config] with
    Has[Auth] with
    ZEnv

  type ApiResult[+R] = ZIO[ApiEnv, ApiError, R]

  type Api_ = Api[ApiResult]
}
