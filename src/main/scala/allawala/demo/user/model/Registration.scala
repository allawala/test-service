package allawala.demo.user.model

import io.circe.generic.JsonCodec

@JsonCodec
case class Registration(email: String, password: String, firstName: String, lastName: String)
