package fun.web.api

import fun.api.Api

import sloth._
import java.nio.ByteBuffer
import boopickle.Default._
import chameleon.ext.boopickle._

import org.http4s.{Method, HttpRoutes, EntityDecoder, MediaType}
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._

import zio._
import zio.interop.catz._
import zio.interop.catz.implicits._

object ApiService {

  private val dsl = Http4sDsl[Task]
  import dsl._

  private val requestDecoder = EntityDecoder.decodeBy[Task, ByteBuffer](MediaType.application.`octet-stream`) { m =>
    EntityDecoder.collectBinary(m).map(chunk => ByteBuffer.wrap(chunk.toArray))
  }

  val service = ZIO.accessM[Has[Api[ApiResult]] with ApiEnv] { env =>

    val apiImpl = env.get[Api[ApiResult]]
    val router = Router[ByteBuffer, ApiResult].route(apiImpl)

    val httpRoutes = HttpRoutes.of[Task] { case request if request.method == Method.POST =>
      val path = request.pathInfo.segments.map(s => s.encoded).toList
      val decodedRequest = requestDecoder.decode(request, strict = false)

      decodedRequest.value.flatMap {
        case Right(bytes) =>
          router(Request(path, bytes)) match {
            case RouterResult.Success(_, result) =>
              result
                .provide(env)
                .flatMap(b => Ok(b.serialized.array))
                .catchAll {
                  case ApiError.Internal => InternalServerError()
                }
            case RouterResult.Failure(_, result) =>
              result match {
                case ServerFailure.PathNotFound(_) => NotFound()
                case ServerFailure.DeserializerError(_) => BadRequest()
                case ServerFailure.HandlerError(_) => InternalServerError()
              }
          }
        case Left(_) => BadRequest()
      }
    }

    ZIO.succeed(httpRoutes.orNotFound)
  }
}
