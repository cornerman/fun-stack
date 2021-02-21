package fun.web.client

import zio._

import outwatch._
import outwatch.dsl._
import outwatch.z._
import colibri._

object Component {

  val onEnter = onKeyUp.filter(_.key == "Enter")

  val stringToEvent: Observable[String] => Observable[Event] = _
    .filter(_.nonEmpty)
    .map[Event](string => Event.AddTodo(Todo(string)))

  val inputMask = ModifierM.delay {
    val currentValue = Subject.publish[String]
    val clearValue = currentValue.contramap[Event](_ => "")

    div(
      input(
        placeholder := "Type Todo",
        value <--[Observable] currentValue,
        onInput.value --> currentValue,
        onEnter.value.transform(stringToEvent).dispatch,
      ),

      button(
        "Add Todo",
        onClick.useLatest(currentValue).transform(stringToEvent).dispatch,
      ),
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
    ),
  )

  val root = div(
    inputMask,
    todoList,
  )
}
