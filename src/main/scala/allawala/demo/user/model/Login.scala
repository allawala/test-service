package allawala.demo.user.model

import io.circe.generic.JsonCodec

@JsonCodec
case class Login(email: String, password: String, rememberMe: Option[Boolean] = Some(false))
