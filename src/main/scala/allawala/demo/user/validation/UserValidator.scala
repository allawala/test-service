package allawala.demo.user.validation

import allawala.chassis.core.validation.ValidationError
import allawala.demo.core.validation.ValidateEmail
import allawala.demo.user.model.Login
import cats.data.{NonEmptyList, Validated}

trait UserValidator extends ValidateEmail {
  def validateLogin(login: Login): Validated[NonEmptyList[ValidationError], Login]
}
