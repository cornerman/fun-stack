package fun.web.client.aws

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSImport("aws4", JSImport.Default)
object AWS4 extends js.Object {
  def sign(opts: AWS4SignOptions, params: AWS4SignParams): AWS4SignResponse = js.native
}

trait AWS4SignOptions extends js.Object {
  def host: String
  def path: String
  def service: String
  def region: String
  def signQuery: Boolean
}

trait AWS4SignParams extends js.Object {
  def accessKeyId: String
  def secretAccessKey: String
}

@js.native
trait AWS4SignResponse extends js.Object {
  def path: String = js.native
}
