package allawala.demo.user.service

import allawala.ResponseFE
import allawala.chassis.core.exception.{InitializationException, ServerException}
import allawala.chassis.http.lifecycle.BaseLifecycleAware
import allawala.chassis.util.DataGenerator
import allawala.demo.user.entity.Permission
import allawala.demo.user.model.{Registration, User, UserUpdate}
import allawala.demo.user.repository.UserRepository
import allawala.demo.user.translation.UserTranslator
import javax.inject.{Inject, Named}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(userTranslator: UserTranslator, userRepository: UserRepository, dataGenerator: DataGenerator)
                               (@Named("blocking-fixed-pool-dispatcher") implicit val ec: ExecutionContext) extends BaseLifecycleAware with UserService {
  override def preStart(): Future[Either[InitializationException, Unit]] = {
    // Create a test user
    val defaultRegistration = Registration("admin@test.com", "password123", "admin", "admin")
    Future {
      Right {
        // admin has permission to perform all actions on all user instances in this example
        val permission = Permission("user", "*", Set("*"))
        userRepository.create(userTranslator.fromRegistration(dataGenerator.uuidStr, defaultRegistration, Set("admin"), Set(permission)))
      }
    }
  }

  override def preStop(): Future[Either[InitializationException, Unit]] = {
    userRepository.clear()
    Future.successful {
      Right {
        userRepository.clear()
      }
    }
  }

  override def register(registration: Registration): ResponseFE[User] = {
    Future {
      userRepository.getByEmailOpt(registration.email) match {
        case Some(_) => Left(ServerException("email.already.exists", messageParameters = Seq(registration.email)))
        case None =>
          val userUuid = dataGenerator.uuidStr
          /*
            a user has permission to perform all actions on their own instances
            a user has permission to only view other users
           */
          val permissions = Set(
            Permission("user", "*", Set(userUuid)),
            Permission("user", "view", Set("*"))
          )
          Right(userTranslator.toModel(userRepository.create(userTranslator.fromRegistration(userUuid, registration, Set("client"), permissions))))
      }
    }
  }

  override def getUser(uuid: String): ResponseFE[Option[User]] = {
    Future.successful(Right(userRepository.getOpt(uuid).map(userTranslator.toModel)))
  }

  override def updateUser(uuid: String, userUpdate: UserUpdate): ResponseFE[User] = {
    Future {
      userRepository.getOpt(uuid) match {
        case Some(entity) => Right(userTranslator.toModel(userRepository.update(userTranslator.fromUpdate(entity, userUpdate))))
        case None => Left(ServerException("user.not.found", messageParameters = Seq(uuid)))
      }
    }
  }

  override def login(email: String): ResponseFE[User] = {
    // Do things like reset failed login attempts back to 0
    Future.successful(Right(userTranslator.toModel(userRepository.getByEmail(email))))
  }

  override def loginFailed(email: String): ResponseFE[Unit] = {
    // Do things like keep track of consecutive failed login attempts, lock the user after the threshold is met etc
    Future.successful(Right(()))
  }
}
