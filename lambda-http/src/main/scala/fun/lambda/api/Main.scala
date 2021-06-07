package fun.lambda.api

import scala.scalajs.js
import net.exoego.facade.aws_lambda._
import fun.api.http._
import cats.effect.IO
import scala.scalajs.js.JSConverters._

object Main {
  import scala.concurrent.ExecutionContext.Implicits.global

  @js.annotation.JSExportTopLevel("handler")
  val handler: js.Function2[APIGatewayProxyEventV2, Context, js.Promise[APIGatewayProxyStructuredResultV2]] = { (event, context) =>
    println(js.JSON.stringify(event))
    println(js.JSON.stringify(context))

    val interpreter = LambdaServerInterpreter[IO](event)
    interpreter(new LambdaServerRequest(event), Api.booksListingImpl :: Nil)
      .map {
        case Some(response) => println("GOT RESPONSE", response); response.body.get
        case None           => println("GOT NO RESPONSE"); ???
      }
      .unsafeToFuture()
      .toJSPromise
  }
}
