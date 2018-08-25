package allawala.demo.user.validation

import allawala.chassis.core.validation.ValidationError
import allawala.demo.user.model.{Login, Registration}
import cats.data.{NonEmptyList, Validated}
import cats.implicits._

class UserValidatorImpl extends UserValidator {
  private val MinPasswordLength = 5

  override def validateRegistration(registration: Registration): Validated[NonEmptyList[ValidationError], Registration] = {
    email("email", registration.email) |@|
      notBlank("firstName", registration.firstName) |@|
      notBlank("lastName", registration.lastName) |@|
      minLength("password", registration.password, MinPasswordLength) map {
      case _ => registration
    }
  }

  override def validateLogin(login: Login): Validated[NonEmptyList[ValidationError], Login] = {
    email("email", login.email) |@|
      minLength("password", login.password, MinPasswordLength) map {
      case _ => login
    }
  }
}
