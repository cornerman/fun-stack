package fun.web.api

import fun.api.Command

object StateModifier {
  val modifier: Command => State => State = {
    case Command.IncrementValue => state => state.copy(value = state.value + 1)
    case Command.DecrementValue => state => state.copy(value = state.value - 1)
  }
}
