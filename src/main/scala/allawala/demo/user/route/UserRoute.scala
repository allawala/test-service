package allawala.demo.user.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import allawala.chassis.auth.shiro.route.RouteSecurity
import allawala.chassis.auth.shiro.service.ShiroAuthService
import allawala.chassis.http.route.{HasRoute, RouteSupport}
import allawala.chassis.i18n.service.I18nService
import allawala.demo.user.service.UserService
import javax.inject.Inject

class UserRoute @Inject() (
                            override val i18nService: I18nService,
                            userService: UserService,
                            override val authService: ShiroAuthService) extends HasRoute
  with RouteSupport
  with RouteSecurity {

  override def route: Route = pathPrefix("v1" / "secure") {
    getUser ~
    logout ~
    logoutAllSessions
  }

  def getUser: Route = path("users" / Segment) { uuid =>
    get {
      onAuthenticated { subject =>
        onCompleteEither {
          userService.getUser(uuid)
        }
      }
    }
  }

  def logout: Route = path("users" / Segment / "logout") { _ =>
    post {
      onInvalidateSession {
        complete(StatusCodes.OK)
      }
    }
  }

  def logoutAllSessions: Route = path("users" / Segment / "logout-all-sessions") { _ =>
    post {
      onInvalidateAllSessions {
        complete(StatusCodes.OK)
      }
    }
  }
}
