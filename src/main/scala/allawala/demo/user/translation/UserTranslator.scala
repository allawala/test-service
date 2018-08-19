package allawala.demo.user.translation

import allawala.demo.user.entity.UserEntity
import allawala.demo.user.model.{Registration, User}

trait UserTranslator {
  def fromRegistration(registration: Registration): UserEntity
  def toModel(entity: UserEntity): User
}
