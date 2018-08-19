package allawala.demo.user.repository

import allawala.demo.user.entity.UserEntity

trait UserRepository {
  def create(userEntity: UserEntity): UserEntity
  def update(userEntity: UserEntity): UserEntity
  def delete(uuid: String): UserEntity
  def getByEmailOpt(email: String): Option[UserEntity]
  def getOpt(uuid: String): Option[UserEntity]
}
