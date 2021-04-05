package fun.web.client

import io.estatico.newtype.macros.newtype

package object aws {
  case class Url(value: String)
  case class ClientId(value: String)
  case class Region(value: String)
  case class IdentityPoolId(value: String)
}
