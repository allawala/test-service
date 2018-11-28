package allawala.demo.user.entity

/*
  you should make the resource, action into enums
 */
case class Permission(resource: String, action: String, instances: Set[String]) {
  val permissionString = s"$resource:$action:${instances.mkString(",")}"
}

case class UserEntity(
                       uuid: String,
                       email: String,
                       firstName: String,
                       lastName: String,
                       encryptedPassword: String,
                       salt: String,
                       roles: Set[String] = Set.empty[String],
                       permissions: Set[Permission] = Set.empty[Permission]
                     )
