package allawala.demo.user.entity

case class UserEntity(
                       uuid: String,
                       email: String,
                       firstName: String,
                       lastName: String,
                       encryptedPassword: String,
                       salt: String
                     )
