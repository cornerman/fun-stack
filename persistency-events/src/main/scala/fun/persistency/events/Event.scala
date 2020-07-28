package fun.persistency.events

sealed trait Event
object Event {
  case object IncrementValue extends Event
}
