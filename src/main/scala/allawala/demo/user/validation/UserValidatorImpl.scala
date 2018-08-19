package allawala.demo.user.validation
import allawala.chassis.core.validation.ValidationError
import allawala.demo.user.model.Login
import cats.data.{NonEmptyList, Validated}

class UserValidatorImpl extends UserValidator {
  override def validateLogin(login: Login): Validated[NonEmptyList[ValidationError], Login] = {
    email("email", login.email) map {
      case _ => login
    }
  }
}
