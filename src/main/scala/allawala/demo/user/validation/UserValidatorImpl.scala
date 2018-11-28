package allawala.demo.user.validation

import allawala.ValidationResult
import allawala.demo.user.model.{Login, Registration, UserUpdate}
import cats.implicits._

class UserValidatorImpl extends UserValidator {
  private val MinPasswordLength = 5

  override def validateRegistration(registration: Registration): ValidationResult[Registration] = {
    (
      email("email", registration.email),
      notBlank("firstName", registration.firstName),
      notBlank("lastName", registration.lastName),
      minLength("password", registration.password, MinPasswordLength)
    ) mapN {
      case _ => registration
    }
  }

  override def validateLogin(login: Login): ValidationResult[Login] = {
    (
      email("email", login.email),
      minLength("password", login.password, MinPasswordLength)
    ) mapN {
      case _ => login
    }
  }

  override def validateUpdate(userUpdate: UserUpdate): ValidationResult[UserUpdate] = {
    (
      notBlank("firstName", userUpdate.firstName),
      notBlank("lastName", userUpdate.lastName)
    ) mapN {
      case _ => userUpdate
    }

  }
}
