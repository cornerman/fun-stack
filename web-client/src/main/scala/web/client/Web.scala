package fun.web.client

import zio._

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
      .via(clear.contramap[Event](_ => ()))
      .contramap[String](string => Event.AddTodo(Todo(string)))
      .contrafilter(_.nonEmpty)

    div(
      input(
        placeholder := "Type Todo",
        value <-- clear.map(_ => ""),
        EmitterBuilder.fromSource(clear).map(_ => "") --> currentValue,
        onInput.value --> currentValue,
        onEnter.value.transformSink(sink).dispatch
      ),

      button(
        "Add Todo",
        onClick.useLatest(currentValue).transformSink(sink).dispatch
      )
    )
  }

  def todoItem(todo: Todo) = ModifierM(
    span(todo.text, marginRight := "20px"),
    button("Remove", onClick.use[Event](Event.RemoveTodo(todo)).dispatch),
  )

  val todoList = div(
    b("Todos"),
    ul(
      ModifierM.accessM[WebEnv](_.get[Config].todoList.map { list =>
        list.todos.map(todo => li(todoItem(todo)))
      } : ModifierM[WebEnv])
    )
  )

  val root = div(
    inputMask,
    todoList,
  )
}
