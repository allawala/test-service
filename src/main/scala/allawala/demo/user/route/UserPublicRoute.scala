package allawala.demo.user.route

import akka.http.scaladsl.server.Route
import allawala.chassis.http.route.{HasRoute, RouteSupport}
import allawala.chassis.i18n.service.I18nService
import allawala.demo.user.model.Login
import allawala.demo.user.service.UserService
import javax.inject.Inject

class UserPublicRoute @Inject() (
                                  override val i18nService: I18nService,
                                  userService: UserService
                                ) extends HasRoute with RouteSupport {
  override def route: Route = pathPrefix("v1" / "public") {
    register
  }

  def register: Route = path("users" / "register") {
    post {
      entity(as[Login]) { login =>
        onCompleteEither {
          userService.register(login)
        }
      }
    }
  }
}