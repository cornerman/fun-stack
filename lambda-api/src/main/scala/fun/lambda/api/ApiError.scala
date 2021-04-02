package fun.lambda.api

sealed trait ApiError
object ApiError {
  case object BadRequest extends ApiError
  case object Internal extends ApiError
  case class ServerFailure(msg: String) extends ApiError
}
