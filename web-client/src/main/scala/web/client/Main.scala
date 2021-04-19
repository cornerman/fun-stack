package fun.web.client

import fun.web.client.data._
import fun.web.client.aws._

import zio._
import zio.console._
import zio.internal.Platform
import zio.interop.catz._

import colibri._
import outwatch.{Outwatch, EventDispatcher}
import org.scalajs.dom

object Main extends App {

  dom.console.log("AppConfig", AppConfig)

  def run(args: List[String]) =
    appLogic
      .provideCustomLayer(appLayer)
      .exitCode

  private val config = for {
    todoList <- ZIO(Subject.behavior(TodoList.initial))
    config  = Config(todoList)
    updater = EventDispatcher.ofModelUpdate(todoList, Event.update _)
  } yield Has[Config](Config(todoList)) ++ Has[EventDispatcher[Event]](updater)

  private val appLayer =
    ZLayer.fromEffectMany(config) ++
      ZLayer.succeed[Api_](WsClient.api) ++
      ZLayer.succeed[Platform](Platform.default)

  private val render = ZIO.accessM[WebEnv] { env =>
    Outwatch.renderInto[Task]("#app", Component.root.provide(env))
  }

  private val appLogic = for {
    _ <- render
  } yield ()
}
