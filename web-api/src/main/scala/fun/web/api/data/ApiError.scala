package fun.web.api

sealed trait ApiError
object ApiError {
  case object Internal extends ApiError
}
