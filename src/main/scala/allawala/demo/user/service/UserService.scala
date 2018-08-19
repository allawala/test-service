package allawala.demo.user.service

import allawala.ResponseFE
import allawala.demo.user.model.{Login, User}

trait UserService {
  def register(login: Login): ResponseFE[User]
}
