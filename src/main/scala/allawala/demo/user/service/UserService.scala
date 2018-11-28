package allawala.demo.user.service

import allawala.ResponseFE
import allawala.demo.user.model.{Registration, User, UserUpdate}

trait UserService {
  def register(registration: Registration): ResponseFE[User]
  def getUser(uuid: String): ResponseFE[Option[User]]
  def updateUser(uuid: String, userUpdate: UserUpdate): ResponseFE[User]

  def login(email: String): ResponseFE[User]
  def loginFailed(email: String): ResponseFE[Unit]
}
