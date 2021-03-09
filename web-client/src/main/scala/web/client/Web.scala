package fun.web.client

import fun.web.client.data._

import colibri._
import outwatch._
import outwatch.dsl._
import outwatch.z._

import cats.effect.SyncIO
import zio._

object Component {

  val onEnter = onKeyUp.filter(_.key == "Enter")

  val inputToEvent: PartialFunction[String, SyncIO[Event]] = {
    case input if input.nonEmpty => Todo.create(input).map(Event.AddTodo)
  }

  def checkedToEvent(todoId: TodoId): Boolean => Event = {
    case true => Event.DoneTodo(todoId)
    case false => Event.UndoneTodo(todoId)
  }

  val inputMask = for {
    currentValue <- SyncIO(Subject.publish[String])
  } yield div(
    input(
      tpe := "text",
      placeholder := "Type Todo",
      value <--[Observable] currentValue,
      onInput.value --> currentValue,
      onEnter.value.collect(inputToEvent).mapSync(identity).dispatch,
    ),

    button(
      "Add Todo",
      onClick.useLatest(currentValue).collect(inputToEvent).mapSync(identity).dispatch,
    ),
  )

  def checkbox(todoList: TodoList, todoId: TodoId) = input(
    marginRight := "0px 10px",

    tpe := "checkbox",
    checked := todoList.isDone(todoId),
    onInput.checked.map(checkedToEvent(todoId)).dispatch,
  )

  def todoItem(todoList: TodoList, todo: Todo) = div(
    Modifier.ifTrue(todoList.isDone(todo.id))(opacity := 0.5),

    checkbox(todoList, todo.id),
    span(todo.content.text, marginRight := "20px"),
    button("Remove", onClick.use[Event](Event.RemoveTodo(todo.id)).dispatch),
  )

  def todoItems(todoList: TodoList) = {
    val (undoneTodos, doneTodos) = todoList.todos.partition(todo => todoList.isDone(todo.id))

    ModifierM[WebEnv](
      b("Todos"),
      doneTodos.map(todoItem(todoList, _)),

      b("Done"),
      undoneTodos.map(todoItem(todoList, _))
    )
  }

  val root = div(
    div(
      inputMask
    ),
    div(
      ModifierM.accessM[WebEnv](_.get[Config].todoList.map(todoItems))
    ),
  )
}
