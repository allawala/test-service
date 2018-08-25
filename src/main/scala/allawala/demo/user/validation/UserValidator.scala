package allawala.demo.user.validation

import allawala.chassis.core.validation.{Validate, ValidationError}
import allawala.demo.core.validation.ValidateEmail
import allawala.demo.user.model.{Login, Registration}
import cats.data.{NonEmptyList, Validated}

trait UserValidator extends ValidateEmail with Validate {
  def validateRegistration(registration: Registration): Validated[NonEmptyList[ValidationError], Registration]
  def validateLogin(login: Login): Validated[NonEmptyList[ValidationError], Login]
}
