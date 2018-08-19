package allawala.demo.user.service

import allawala.ResponseFE
import allawala.chassis.util.DataGenerator
import allawala.demo.user.model.{Login, User}
import javax.inject.Inject

import scala.concurrent.Future

class UserServiceImpl @Inject()(dataGenerator: DataGenerator) extends UserService {
  override def register(login: Login): ResponseFE[User] = {
    Future.successful(Right(User(Some(dataGenerator.uuidStr), login.email)))
  }
}
