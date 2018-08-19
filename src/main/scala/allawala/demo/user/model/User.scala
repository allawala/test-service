package allawala.demo.user.model

import io.circe.generic.JsonCodec

@JsonCodec
case class User(uuid: Option[String] = None, email: String)
