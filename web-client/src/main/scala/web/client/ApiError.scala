package fun.web.client

sealed trait ApiError
object ApiError {
  case object BadResponse extends ApiError
  case class RequestFailed(error: Throwable) extends ApiError
  case class ClientFailure(failure: sloth.ClientFailure) extends ApiError

  implicit val failureConvert: sloth.ClientFailureConvert[ApiError] = new sloth.ClientFailureConvert[ApiError] {
    def convert(failure: sloth.ClientFailure) = ApiError.ClientFailure(failure)
  }
}
