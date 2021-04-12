package fun.lambda.api

import zio._

trait Database {
  def getState: UIO[State]
  def setState(state: State): UIO[Unit]
  def updateState(update: State => State): UIO[Unit]
}

class DatabaseLive private(ref: Ref[State]) extends Database {
  override def getState = ref.get
  override def setState(state: State) = ref.set(state)
  override def updateState(update: State => State) = ref.update(update)
}

object DatabaseLive {
  def create = {
    println("CALL DB CREATE")
    Ref.make(State.initial).tap(_ => UIO(println("CReaTING"))).map(new DatabaseLive(_))
  }
}
