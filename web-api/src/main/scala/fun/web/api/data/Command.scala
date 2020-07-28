package fun.web.api.data

sealed trait Command
object Command {
  case object IncrementValue extends Command
}
