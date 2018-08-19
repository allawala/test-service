package allawala.demo.user.service

import allawala.ResponseFE
import allawala.chassis.core.exception.ServerException
import allawala.demo.user.model.{Registration, User}
import allawala.demo.user.repository.UserRepository
import allawala.demo.user.translation.UserTranslator
import javax.inject.{Inject, Named}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(userTranslator: UserTranslator, userRepository: UserRepository)
                               (@Named("blocking-fixed-pool-dispatcher") implicit val ec: ExecutionContext
                               ) extends UserService {
  override def register(registration: Registration): ResponseFE[User] = {
    Future {
      userRepository.getByEmailOpt(registration.email) match {
        case Some(_) => Left(ServerException("email.already.exists", messageParameters = Seq(registration.email)))
        case None => Right(userTranslator.toModel(userRepository.create(userTranslator.fromRegistration(registration))))
      }
    }
  }

  override def getUser(uuid: String): ResponseFE[Option[User]] = {
    Future.successful(Right(userRepository.getOpt(uuid).map(userTranslator.toModel)))
  }
}
