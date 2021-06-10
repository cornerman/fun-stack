package fun.api.http

import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import io.circe.generic.auto._

object types {
  type Limit     = Int
  type AuthToken = String
}
import types._

case class BooksFromYear(genre: String, year: Int)
case class Book(title: String)

object Api {
  val booksListing: Endpoint[(BooksFromYear, Limit, AuthToken), String, List[Book], Any] =
    endpoint.get
      .in(("books" / path[String]("genre") / path[Int]("year")).mapTo(BooksFromYear))
      .in(query[Limit]("limit").description("Maximum number of books to retrieve"))
      .in(header[AuthToken]("X-Auth-Token"))
      .errorOut(stringBody)
      .out(jsonBody[List[Book]])
}

// object Main extends App {
//   import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
//   import sttp.tapir.openapi.circe.yaml._
//   import java.nio.charset.StandardCharsets
//   import java.nio.file.{Paths, Files}
//   import scala.util.{Try, Success, Failure}

//   val docs         = OpenAPIDocsInterpreter.toOpenAPI(Api.booksListing, "My Bookshop", "1.0")
//   val path         = Paths.get(s"swagger-api.yaml")
//   val schemaString = docs.toYaml
//   Try(Files.write(path, schemaString.getBytes(StandardCharsets.UTF_8))) match {
//     case Success(path)  => println(s"Written schema file to $path")
//     case Failure(error) => println(s"Error writing schema file: $error")
//   }
// }
