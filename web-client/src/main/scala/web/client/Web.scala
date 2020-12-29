package fun.web.client

import fun.api.Command

import zio._
import zio.interop.catz._

import outwatch._
import outwatch.dsl._
import outwatch.z._
import colibri._

object Component {

  val onEnter = onKeyUp.filter(_.key == "Enter")

  val inputMask = ModifierM.delay {

    val currentValue = Subject.publish[String]
    val clear = Subject.publish[Unit]

    val sink: Observer[Event] => Observer[String] = _
      .contramap[String](string => Event.AddTodo(Todo(string)))
      .contrafilter(_.nonEmpty)
      .via(clear.contramap[String](_ => ()))

    div(
      input(
        placeholder := "Type Todo",
        value <-- clear.map(_ => ""),
        onInput.value --> currentValue,
        onEnter.value.transformSink(sink).dispatch
      ),

      button(
        "Add Todo",
        onClick.useLatest(currentValue).transformSink(sink).dispatch
      )
    )
  }

  val todoList = div(
    b("Todos"),
    ul(
      ModifierM.access[WebEnv](_.get[Config].todoList.map(_.todos.map { todo =>
        li(todo.text)
      }))
    )
  )

  val root = div(
    inputMask,
    todoList,
  )
}
