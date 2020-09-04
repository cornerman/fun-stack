package fun.web.api

import fun.api.Api

import sloth._
import java.nio.ByteBuffer
import boopickle.Default._
import chameleon.ext.boopickle._

import org.http4s.{Method, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.middleware._

import zio._
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.console._

import scala.util.control.{NoStackTrace, NonFatal}
import scala.concurrent.duration._

case class ServerConfig(interface: String, port: Int)
object ServerConfig {
  val default = ServerConfig(interface = "localhost", port = 8080)
}

object Server {

  private val corsConfig = CORSConfig(
    anyOrigin = true,
    anyMethod = false,
    allowedMethods = Some(Set("GET", "POST")),
    allowCredentials = false,
    maxAge = 1.day.toSeconds)

  private val server = ZIO.runtime[Any].flatMap { implicit rts =>
    for {
      service <- ApiService.service
      _ <- ZIO.accessM[Has[ServerConfig]] { env =>
        val config = env.get[ServerConfig]
        BlazeServerBuilder[Task](scala.concurrent.ExecutionContext.global)
          .bindHttp(config.port, config.interface)
          .withHttpApp(CORS(service, corsConfig))
          .serve
          .compile
          .drain
      }
    } yield ()
  }

  val run: URIO[
    Has[ServerConfig] with
    Has[Api[ApiResult]] with
    ApiEnv,
    Unit
  ] = ZIO.accessM { env =>
    for {
      _ <- putStrLn(s"Starting Server: ${env.get[ServerConfig]}")
      _ <- server.orDie
      _ <- putStrLn("Stopping Server")
    } yield ()
  }
}

private object ApiService {

  private case class ApiThrowable(error: ApiError) extends NoStackTrace

  private val dsl = Http4sDsl[Task]
  import dsl._

  val service = ZIO.accessM[Has[Api[ApiResult]] with ApiEnv] { env =>

    val router = Router[ByteBuffer, ApiResult]
      .route[Api[ApiResult]](env.get[Api[ApiResult]])

    ZIO.succeed(HttpRoutes.of[Task] { case request if request.method == Method.POST =>
      val path = request.pathInfo.segments.map(s => s.encoded).toList
      val result = router(Request(path, null))

      result match {
        case RouterResult.Success(_, result) =>
          val response = result
            .provide(env)
            .map(_.serialized.array)
            .mapError(ApiThrowable(_))

          Ok(response).catchSome {
            case ApiThrowable(err) => err match {
              case ApiError.Internal => InternalServerError()
            }
            case NonFatal(_) => InternalServerError()
          }

        case RouterResult.Failure(_, result) =>
          result match {
            case ServerFailure.PathNotFound(_) => NotFound()
            case ServerFailure.DeserializerError(_) => BadRequest()
            case ServerFailure.HandlerError(_) => InternalServerError()
          }
      }
    }.orNotFound)
  }

}
