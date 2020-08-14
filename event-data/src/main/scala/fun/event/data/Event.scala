package fun.event.data

sealed trait Event
object Event {
  case object IncrementValue extends Event
}
