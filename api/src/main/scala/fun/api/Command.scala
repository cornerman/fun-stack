package fun.api

sealed trait Command
object Command {
  case object IncrementValue extends Command
  case object DecrementValue extends Command
}
