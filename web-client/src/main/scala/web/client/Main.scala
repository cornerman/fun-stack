package fun.web.client

import zio._
import zio.console._
import zio.internal.Platform
import zio.interop.catz._

import colibri._
import outwatch.{Outwatch, EventDispatcher}

object Main extends App {

  def run(args: List[String]) = {
    appLogic
      .provideCustomLayer(appLayer)
      .exitCode
  }

  private val config = ConfigF(
    todoList = Subject.behavior(TodoList.initial)
  )

  private val appLayer =
    ZLayer.succeed[Config](config) ++
    ZLayer.succeed[EventDispatcher[Event]](EventDispatcher.ofModelUpdate(config.todoList, Event.update _)) ++
    ZLayer.succeed[Api_](HttpClient.api) ++
    ZLayer.succeed[Platform](Platform.default)

  private val render = ZIO.accessM[WebEnv] { env =>
    Outwatch.renderInto[Task]("#app", Component.root.provide(env))
  }

  private val appLogic = for {
    _ <- render
  } yield ()
}

