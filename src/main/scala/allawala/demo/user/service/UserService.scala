package allawala.demo.user.service

import allawala.ResponseFE
import allawala.demo.user.model.{Registration, User}

trait UserService {
  def register(registration: Registration): ResponseFE[User]
  def getUser(uuid: String): ResponseFE[Option[User]]
}
