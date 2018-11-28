package allawala.demo.user.validation

import allawala.ValidationResult
import allawala.chassis.core.validation.Validate
import allawala.demo.core.validation.ValidateEmail
import allawala.demo.user.model.{Login, Registration, UserUpdate}

trait UserValidator extends ValidateEmail with Validate {
  def validateRegistration(registration: Registration): ValidationResult[Registration]
  def validateLogin(login: Login): ValidationResult[Login]
  def validateUpdate(userUpdate: UserUpdate): ValidationResult[UserUpdate]
}
