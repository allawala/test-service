package allawala.demo.user.route

import akka.http.scaladsl.server.Route
import allawala.chassis.auth.shiro.route.RouteSecurity
import allawala.chassis.auth.shiro.service.ShiroAuthService
import allawala.chassis.http.route.{HasRoute, RouteSupport, ValidationDirective}
import allawala.chassis.i18n.service.I18nService
import allawala.demo.user.model.{Login, Registration}
import allawala.demo.user.service.UserService
import allawala.demo.user.validation.UserValidator
import javax.inject.Inject

class UserPublicRoute @Inject()(
                                 override val i18nService: I18nService,
                                 override val authService: ShiroAuthService,
                                 userService: UserService,
                                 userValidator: UserValidator
                               ) extends HasRoute with RouteSupport with ValidationDirective with RouteSecurity {
  override def route: Route = pathPrefix("v1" / "public") {
    register ~
      login
  }

  def register: Route = path("users" / "register") {
    post {
      model(as[Registration])(userValidator.validateRegistration) { validated =>
        onCompleteEither {
          userService.register(validated)
        }
      }
    }
  }

  def login: Route = path("users" / "login") {
    post {
      model(as[Login])(userValidator.validateLogin) { validatedLogin =>
        onAuthenticateWithFailureHandling(validatedLogin.email, validatedLogin.password, validatedLogin.rememberMe.getOrElse(false)) {
          userService.loginFailed(validatedLogin.email)
        } { subject =>
          onCompleteEither {
            userService.login(subject.getPrincipal.asInstanceOf[String])
          }
        }
      }
    }
  }
}
