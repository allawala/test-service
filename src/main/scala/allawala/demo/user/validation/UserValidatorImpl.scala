package allawala.demo.user.validation
import allawala.chassis.core.validation.ValidationError
import allawala.demo.user.model.Registration
import cats.data.{NonEmptyList, Validated}
import cats.implicits._

class UserValidatorImpl extends UserValidator {
  override def validateRegistration(registration: Registration): Validated[NonEmptyList[ValidationError], Registration] = {
    email("email", registration.email) |@|
    minLength("password", registration.password, 5) map {
      case _ => registration
    }
  }
}
