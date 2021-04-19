package fun.web.client

import fun.web.client.data._
import fun.web.client.aws.Fun

import colibri._
import outwatch._
import outwatch.dsl._
import outwatch.z._

import cats.effect.SyncIO
import zio._
import zio.interop.catz._
import zio.interop.catz.implicits._

object Component {
  implicit val r: Runtime[Any]              = Runtime.default
  val F: cats.effect.ConcurrentEffect[Task] = implicitly

  val onEnter = onKeyUp.filter(_.key == "Enter")

  val inputToEvent: PartialFunction[String, SyncIO[Event]] = {
    case input if input.nonEmpty => Todo.create(input).map(Event.AddTodo)
  }

  def checkedToEvent(todoId: TodoId): Boolean => Event = {
    case true  => Event.DoneTodo(todoId)
    case false => Event.UndoneTodo(todoId)
  }

  val inputMask = for {
    currentValue <- SyncIO(Subject.publish[String])
  } yield ModifierM(
    input(
      tpe := "text",
      placeholder := "Type Todo",
      value <-- [Observable] currentValue,
      onInput.value --> currentValue,
      onEnter.value.collect(inputToEvent).via(currentValue.contramap[Any](_ => "")).mapSync(identity).dispatch,
    ),
    button(
      "Add Todo",
      onClick
        .useLatest(currentValue)
        .collect(inputToEvent)
        .via(currentValue.contramap[Any](_ => ""))
        .mapSync(identity)
        .dispatch,
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
      div(
        b("Todos"),
        doneTodos.map(todoItem(todoList, _)),
      ),
      div(
        b("Done"),
        undoneTodos.map(todoItem(todoList, _)),
      ),
    )
  }

  val login = div(
    ModifierM.access[WebEnv] { env =>
      Fun.auth.currentUser.map {
        case Some(user) => button(s"Logout (${user.info.email})", onClick.doAsync(Fun.auth.logout))
        case None       => button("Login", onClick.doAsync(Fun.auth.login))
      }
    },
  )

  val apiInteraction: ModifierM[WebEnv] = div(
    ModifierM.accessM[WebEnv] { env =>
      ModifierM(
        env
          .get[Api_]
          .getState
          // .mapError(x => new Exception(x.toString))
          .map(_.toString),
        button(
          "PRESS",
          onClick.doAsync(env.get[Api_].sendCommand(fun.api.Command.IncrementValue)),
        ),
      )
    },
  )

  val root = div(
    div(
      login,
    ),
    div(
      apiInteraction,
    ),
    div(
      inputMask,
    ),
    div(
      ModifierM.accessM[WebEnv](_.get[Config].todoList.map(todoItems)),
    ),
  )
}
