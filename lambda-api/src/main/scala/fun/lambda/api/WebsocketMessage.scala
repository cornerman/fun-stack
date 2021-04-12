// package fun.lambda.api

// import fun.api.Api

// import cats.effect.{ContextShift, IO}

// import scalajs.js
// import scala.concurrent.{Future, ExecutionContext}

// object WebsocketMessage {
//   private implicit val cs = IO.contextShift(ExecutionContext.global)

//   def push() = ZIO.fromFuture { implicit ec =>
//     ???
//   }

//   // private def sendMessage(url: String, connectionId: String, payload: String): IO[Unit] = IO.fromFuture(IO {
//   //   val url = s"$url/@connections/$connectionId"
//   //   Fetch.fetch(url, new RequestInit {
//   //     method = HttpMethod.POST
//   //     body = payload
//   //     headers = js.Array(js.Array("Content-Type", "application/x-www-form-urlencoded"))
//   //   }).`then`[js.Promise[js.Any]](_.json()).toFuture
//   // }).map(r => js.Object.assign(js.Dynamic.literal(refresh_token = refreshToken), r).asInstanceOf[TokenResponse])
// }
