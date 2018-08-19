package allawala.demo.user.model

import io.circe.generic.JsonCodec

@JsonCodec
case class Login(email: String, password: String)
