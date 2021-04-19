package fun.web.client.aws

// import cats.effect.IO
// import sloth.{Client, ClientException}
// import mycelium.js.core.JsMessageBuilder

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

  val ws = new Websocket(
    WebsocketConfig(
      baseUrl = Url(s"wss://${AppConfig.domainWS}"),
    ),
  )

  // def wsClient[PickleType: JsMessageBuilder] = Client[PickleType, IO, ClientException](websocket.transport[String, String, PickleType])
}
