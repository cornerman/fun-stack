package fun.web.client.data

import cats.effect.SyncIO

case class TodoId(value: String)

case class TodoContent(text: String)

case class TodoHistory(creation: Timestamp)

case class Todo(id: TodoId, content: TodoContent, history: TodoHistory)
object Todo {
  def create(text: String) = for {
    id <- SyncIO(cuid.Cuid())
    now <- Timestamp.now
  } yield Todo(TodoId(id), TodoContent(text), TodoHistory(creation = now))
}

case class TodoList(
  todos: List[Todo],
  isDone: Set[TodoId],
)
object TodoList {
  def initial = TodoList(todos = Nil, isDone = Set.empty)
}
