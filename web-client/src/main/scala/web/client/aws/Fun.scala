package fun.web.client.aws

import cats.effect.IO
import sloth.{Client, ClientException}
import mycelium.js.core.JsMessageBuilder
import mycelium.core.message._
import chameleon.{Serializer, Deserializer}

import org.scalajs.dom

object Fun {
  val auth = new Auth(
    AuthConfig(
      baseUrl = Url(s"https://${AppConfig.domainAuth}"),
      redirectUrl = Url(dom.window.location.origin.getOrElse(AppConfig.domain)),
      clientId = ClientId(AppConfig.clientIdAuth),
      region = Region(AppConfig.region),
      identityPoolId = IdentityPoolId(AppConfig.identityPoolId),
      cognitoEndpoint = Url(AppConfig.cognitoEndpoint),
    ),
  )

  object api {
    import fun.web.client.aws.Base64Serdes._

    val ws = new Websocket(WebsocketConfig(baseUrl = Url(s"wss://${AppConfig.domainWS}")))

    def wsClient[PickleType](implicit
        serializer: Serializer[ClientMessage[PickleType], String],
        deserializer: Deserializer[ServerMessage[PickleType, String, String], String],
    ) =
      Client[PickleType, IO, ClientException](ws.transport[String, String, PickleType])
  }
}
