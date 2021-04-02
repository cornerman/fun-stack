package fun.lambda.api

import scala.scalajs.js

trait LambdaEvent extends js.Object {
  var path: js.UndefOr[js.Array[String]] = js.undefined
  var payload: js.UndefOr[String] = js.undefined
}

trait LambdaResponse extends js.Object {
  var response: js.UndefOr[String] = js.undefined
  var error: js.UndefOr[String] = js.undefined
}
