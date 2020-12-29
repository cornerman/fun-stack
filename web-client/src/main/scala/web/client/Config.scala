package fun.web.client

import outwatch.EventDispatcher
import colibri._

case class Todo(
  text: String,
)

case class TodoList(
  todos: List[Todo],
  dones: List[Todo],
)
object TodoList {
  def initial = TodoList(todos = Nil, dones = Nil)
}

case class ConfigF[+F[_]](
  todoList: F[TodoList]
)

sealed trait Event
object Event {
  case class AddTodo(todo: Todo) extends Event
  case class DoneTodo(todo: Todo) extends Event
  case class RemoveTodo(todo: Todo) extends Event

  def update(event: Event, todoList: TodoList): TodoList = event match {
    case AddTodo(todo) => todoList.copy(todos = todo :: todoList.todos)
    case DoneTodo(todo) => todoList.copy(todos = todoList.todos.filter(_ != todo), dones = todo :: todoList.dones)
    case RemoveTodo(todo) => todoList.copy(todos = todoList.todos.filter(_ != todo), dones = todoList.dones.filter(_ != todo))
  }
}
