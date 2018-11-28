package allawala.demo.user.translation

import allawala.demo.user.entity.{Permission, UserEntity}
import allawala.demo.user.model.{Registration, User, UserUpdate}

trait UserTranslator {
  def fromRegistration(uuid: String, registration: Registration, roles: Set[String], permissions: Set[Permission]): UserEntity
  def fromUpdate(userEntity: UserEntity, userUpdate: UserUpdate): UserEntity
  def toModel(entity: UserEntity): User
}
