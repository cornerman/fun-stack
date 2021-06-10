package fun.lambda.http

import scala.scalajs.js
import fun.api.http._
import funstack.lambda.http.Handler
import cats.effect.IO

object Main {
  val booksListingImpl = Api.booksListing.serverLogic[IO]({ case (_, _, _) => IO.pure(Right(Nil)) })

  @js.annotation.JSExportTopLevel("handler")
  val handler = Handler.handle[IO](booksListingImpl :: Nil)
}
