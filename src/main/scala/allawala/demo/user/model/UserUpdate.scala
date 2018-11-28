package allawala.demo.user.model

import io.circe.generic.JsonCodec

// Separate from the User model, to keep things simple for the demo, and not have to worry about checking uniqueness constraints for email on update
@JsonCodec
case class UserUpdate(firstName: String, lastName: String)
