package allawala.demo.user.model

import io.circe.generic.JsonCodec

/*
  IMPORTANT! never expose password/salt etc back in the response
 */
@JsonCodec
case class User(uuid: String, email: String, firstName: String, lastName: String)
