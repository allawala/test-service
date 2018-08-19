package allawala.demo.user.route

import akka.http.scaladsl.server.Route
import allawala.chassis.http.route.{HasRoute, RouteSupport, ValidationDirective}
import allawala.chassis.i18n.service.I18nService
import allawala.demo.user.model.Login
import allawala.demo.user.service.UserService
import allawala.demo.user.validation.UserValidator
import javax.inject.Inject

class UserPublicRoute @Inject() (
                                  override val i18nService: I18nService,
                                  userService: UserService,
                                  userValidator: UserValidator
                                ) extends HasRoute with RouteSupport with ValidationDirective {
  override def route: Route = pathPrefix("v1" / "public") {
    register
  }

  def register: Route = path("users" / "register") {
    post {
      model(as[Login])(userValidator.validateLogin) { validatedLogin =>
        onCompleteEither {
          userService.register(validatedLogin)
        }
      }
    }
  }
}
