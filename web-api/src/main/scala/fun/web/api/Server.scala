package fun.web.api

import fun.api.Api

import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{GZip, CORS, CORSConfig}

import zio._
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.console._

import java.util.concurrent.ForkJoinPool
import scala.concurrent.ExecutionContext
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
        val config = env.get
        BlazeServerBuilder[Task](ExecutionContext.fromExecutor(new ForkJoinPool))
          .bindHttp(config.port, config.interface)
          .withHttpApp(CORS(GZip(service), corsConfig))
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
