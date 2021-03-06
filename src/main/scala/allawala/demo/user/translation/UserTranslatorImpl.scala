package allawala.demo.user.translation

import allawala.chassis.util.DataGenerator
import allawala.demo.auth.service.EncryptionService
import allawala.demo.user.entity.{Permission, UserEntity}
import allawala.demo.user.model.{Registration, User, UserUpdate}
import javax.inject.Inject

class UserTranslatorImpl @Inject()(dataGenerator: DataGenerator, encryptionService: EncryptionService) extends UserTranslator {
  // Really basic encryption. you may wish to use something different from production purposes
  // You might want to look into java.security.SecureRandom for salt generation
  override def fromRegistration(uuid: String, registration: Registration, roles: Set[String], permissions: Set[Permission]): UserEntity = {
    val salt = dataGenerator.uuidStr // at the very least different salt for each user
    UserEntity(
      uuid = uuid,
      email = registration.email,
      firstName = registration.firstName,
      lastName = registration.lastName,
      encryptedPassword = encryptionService.encrypt(registration.password, salt),
      salt = salt,
      roles = roles,
      permissions = permissions
    )
  }

  override def fromUpdate(userEntity: UserEntity, userUpdate: UserUpdate): UserEntity = {
    userEntity.copy(
      firstName = userUpdate.firstName,
      lastName = userUpdate.lastName
    )
  }

  override def toModel(entity: UserEntity): User = {
    User(
      uuid = entity.uuid,
      email = entity.email,
      firstName = entity.firstName,
      lastName = entity.lastName
    )
  }

}
