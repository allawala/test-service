package allawala.demo.user.repository

import allawala.demo.user.entity.UserEntity
import scala.collection.mutable.{Map => MutMap}

class UserRepositoryImpl extends UserRepository {
  private val users: MutMap[String, UserEntity] = MutMap.empty

  override def create(userEntity: UserEntity): UserEntity = {
    users.get(userEntity.email) match {
      case Some(_) => throw new IllegalArgumentException("unique constraint violation") // emulating db operation failing uniqueness constraint
      case None =>
        users += (userEntity.email -> userEntity)
        userEntity
    }
  }

  override def update(userEntity: UserEntity): UserEntity = {
    users.get(userEntity.email) match {
      case Some(user) =>
        users += (userEntity.email -> userEntity)
        userEntity
      case None => throw new IllegalArgumentException("missing user") // emulating db operation failing to find the row to update
    }
  }

  override def delete(uuid: String): UserEntity = {
    users.values.find(_.uuid == uuid) match {
      case Some(user) =>
        users -= user.email
        user
      case None => throw new IllegalArgumentException("missing user") // emulating db operation failing to find the row to update
    }
  }

  override def getByEmailOpt(email: String): Option[UserEntity] = users.get(email)

  override def getOpt(uuid: String): Option[UserEntity] = users.values.find(_.uuid == uuid)
}
