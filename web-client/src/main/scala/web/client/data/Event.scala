package fun.web.client.data

sealed trait Event
object Event {
  case class AddTodo(todo: Todo) extends Event
  case class DoneTodo(todo: TodoId) extends Event
  case class UndoneTodo(todo: TodoId) extends Event
  case class RemoveTodo(todo: TodoId) extends Event

  def update(event: Event, todoList: TodoList): TodoList = event match {
    case AddTodo(todo) => println("ADD"); todoList.copy(todos = todo :: todoList.todos.filter(_.id != todo.id))
    case DoneTodo(todoId) =>
      println("DONE" + (todoList.isDone + todoId))
      todoList.copy(isDone = todoList.isDone + todoId)
    case UndoneTodo(todoId) => println("Undone"); todoList.copy(isDone = todoList.isDone - todoId)
    case RemoveTodo(todoId) => println("REMOVE"); todoList.copy(todos = todoList.todos.filter(_.id != todoId))
  }
}
