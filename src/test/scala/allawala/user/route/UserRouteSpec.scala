package allawala.user.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import allawala.common.RouteSpec
import allawala.demo.user.model.UserUpdate
import allawala.demo.user.route.UserRoute
import allawala.demo.user.validation.UserValidator
import cats.data.Validated.Valid
import org.apache.shiro.subject.Subject

import scala.concurrent.Future

class UserRouteSpec extends RouteSpec {
  private val userValidator = mock[UserValidator]

  private lazy val route = Route.seal(handleExceptions(routesExceptionHandler) {
    new UserRoute(i18nService, userService, userValidator, authService).route
  })

  "user route" should {
    "check view instance permissions on get" in {
      new AuthenticatedUser {
        val permission = "user:view:user-uuid"
        authService.isAuthorizedSync(any[Subject], any[String]) returns true
        userService.getUser(equ("user-uuid")) returns Future.successful(Right(Some(currentUser)))

        assertSecureGet(route, "/v1/secure/users/user-uuid", StatusCodes.OK, currentUser)

        // ensures authorization call happen (i.e the action needs to be permitted)
        oneOf(authService).isAuthorizedSync(any[Subject], equ(permission))
      }
    }

    "check update instance permissions on get" in {
      new AuthenticatedUser {
        val update = UserUpdate("new-first-name", "new-last-name")
        val permission = "user:update:user-uuid"
        userValidator.validateUpdate(equ(update)) returns Valid(update)
        authService.isAuthorizedSync(any[Subject], any[String]) returns true
        userService.updateUser(equ("user-uuid"), equ(update)) returns Future.successful(Right(currentUser))

        assertSecurePut(route, "/v1/secure/users/user-uuid", update, StatusCodes.OK, currentUser)

        // ensures authorization call happen (i.e the action needs to be permitted)
        oneOf(authService).isAuthorizedSync(any[Subject], equ(permission))
      }
    }
  }
}
