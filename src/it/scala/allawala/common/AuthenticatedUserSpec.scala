package allawala.common

import akka.http.scaladsl.server.Route
import allawala.chassis.http.route.HasRoute
import allawala.demo.user.model.{Login, User}
import allawala.demo.user.route.UserPublicRoute
import org.scalatest.Assertion

abstract class AuthenticatedUserSpec[T <: HasRoute: Manifest] extends IntegrationSpec[T] {
  // Creating all local users with this password so that its easy to login to test auth
  final val publicUserRoute = Route.seal(
    handleExceptions(routesExceptionHandler) {
      handleRejections(routesRejectionHandler) {
        GuiceUtil.instance[UserPublicRoute].route
      }
    }
  )

  def authenticatedAs(email: String, password: String)(f: (String, String) => Assertion): Assertion = {
    unauthenticatedRequest[Login, User](Post, publicUserRoute, "/v1/public/users/login", Login(email, password)) { user =>
      val token = response.headers.find(_.name == "Authorization")
        .map(_.value()).getOrElse(throw new IllegalStateException("should not happen for integration tests"))

      val result = f(user.uuid, token)
      result
    }
  }
}
