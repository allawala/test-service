package allawala.user.service

import allawala.chassis.util.DataGenerator
import allawala.common.{BaseSpec, FutureSpec}
import allawala.demo.user.entity.{Permission, UserEntity}
import allawala.demo.user.model.{Registration, User, UserUpdate}
import allawala.demo.user.repository.UserRepository
import allawala.demo.user.service.UserServiceImpl
import allawala.demo.user.translation.UserTranslator

import scala.concurrent.Await

class UserServiceSpec extends BaseSpec with FutureSpec {
  private val userTranslator = mock[UserTranslator]
  private val userRepository = mock[UserRepository]
  private val dataGenerator = mock[DataGenerator]

  private val service = new UserServiceImpl(userTranslator, userRepository, dataGenerator)

  before {
    val mocks = Seq(dataGenerator, userTranslator, userRepository)
    reset(mocks:_*)
  }

  "user service" should {
    "successfully register a new user" in {
      val registration = Registration("demo@test.com", "pwd", "first", "last")
      val user = mock[User]
      val entity = mock[UserEntity]
      val permissions = Set(Permission("user", "*", Set("uuid-1")), Permission("user", "view", Set("*")))

      dataGenerator.uuidStr returns "uuid-1"

      userRepository.getByEmailOpt(equ("demo@test.com")) returns None
      userRepository.create(entity) returns entity
      userTranslator.fromRegistration(equ("uuid-1"), equ(registration), equ(Set("client")), equ(permissions)) returns entity
      userTranslator.toModel(equ(entity)) returns user

      val result = Await.result(service.register(registration), timeout)

      result.isRight shouldBe true
      result.right.get should ===(user)
    }

    "fail to register a new user if the email address is not unique" in {
      val registration = Registration("demo@test.com", "pwd", "first", "last")
      val entity = mock[UserEntity]

      userRepository.getByEmailOpt(equ("demo@test.com")) returns Some(entity)

      val result = Await.result(service.register(registration), timeout)

      result.isLeft shouldBe true
      noneOf(userRepository).create(any[UserEntity])
      noneOf(userTranslator).fromRegistration(any[String], any[Registration], any[Set[String]], any[Set[Permission]])
      noneOf(userTranslator).toModel(any[UserEntity])
    }

    "successfully update a user" in {
      val user = mock[User]
      val updateUser = UserUpdate("first", "last")
      val entity = mock[UserEntity]

      userRepository.getOpt(equ("uuid-1")) returns Some(entity)
      userTranslator.fromUpdate(equ(entity), equ(updateUser)) returns entity
      userRepository.update(equ(entity)) returns entity
      userTranslator.toModel(equ(entity)) returns user

      val result = Await.result(service.updateUser("uuid-1", updateUser), timeout)

      result.isRight shouldBe true
      result.right.get should ===(user)
    }

    "fail to update a user if the user is not found" in {
      val updateUser = UserUpdate("first", "last")
      userRepository.getOpt(equ("uuid-1")) returns None


      val result = Await.result(service.updateUser("uuid-1", updateUser), timeout)

      result.isLeft shouldBe true
      noneOf(userTranslator).fromUpdate(any[UserEntity], any[UserUpdate])
      noneOf(userRepository).update(any[UserEntity])
      noneOf(userTranslator).toModel(any[UserEntity])
    }
  }
}