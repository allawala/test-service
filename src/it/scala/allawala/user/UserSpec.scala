package allawala.user

import akka.http.scaladsl.model.StatusCodes
import allawala.chassis.core.model.ErrorEnvelope
import allawala.common.AuthenticatedUserSpec
import allawala.demo.user.model.{Registration, User, UserUpdate}
import allawala.demo.user.route.UserRoute
import org.scalatest.DoNotDiscover

// IMPORTANT, this annotation is required so that the specs are run as part of the suite and not individually as well
@DoNotDiscover
class UserSpec extends AuthenticatedUserSpec[UserRoute] {
  // mutable state, just to make testing easy and get it done and commmitted
  var jordansUuid: String = _
  var pippensUuid: String = _
  "register user" should {
    "fail if it does not meet the validation criteria" in {
      val registration = Registration("jordan@demo.com.au", "mj23", "Michael", "Jordan")
      import io.circe.generic.auto._ // for error envelope
      unauthenticatedRequest[Registration, ErrorEnvelope](Post, publicUserRoute, "/v1/public/users/register", registration) { failure =>
        failure.errorMessage should ===("validation failure")
        failure.details("password").head.message should ===("length should be least at least 5 characters")
      }
    }

    "succeed if the email is unique" in {
      val registration = Registration("jordan@demo.com.au", "mj-23", "Michael", "Jordan")
      unauthenticatedRequest[Registration, User](Post, publicUserRoute, "/v1/public/users/register", registration) { user =>
        jordansUuid = user.uuid
        user.email should ===("jordan@demo.com.au")
        user.firstName should ===("Michael")
        user.lastName should ===("Jordan")
      }
    }

    "fail if the email is not unique" in {
      val registration = Registration("jordan@demo.com.au", "mj-23", "Michael B", "Jordan")
      import io.circe.generic.auto._ // for error envelope
      unauthenticatedRequest[Registration, ErrorEnvelope](Post, publicUserRoute, "/v1/public/users/register", registration) { failure =>
        failure.errorMessage should ===("email jordan@demo.com.au is already in use, please try another email")
      }
    }

    "succeed for a second unique user" in {
      val registration = Registration("pippen@demo.com.au", "pip33", "Scottie", "Pippen")
      unauthenticatedRequest[Registration, User](Post, publicUserRoute, "/v1/public/users/register", registration) { user =>
        pippensUuid = user.uuid
        user.email should ===("pippen@demo.com.au")
        user.firstName should ===("Scottie")
        user.lastName should ===("Pippen")
      }
    }
  }

  "registered user" should {
    "be able to view his own profile" in {
      authenticatedAs("jordan@demo.com.au", "mj-23") { (userUuid, token) =>
        requestNoPayloadWithResponse[User](Get, route, token, s"/v1/secure/users/$userUuid") { user =>
          user.email should ===("jordan@demo.com.au")
        }
      }
    }

    "be able to update his own profile" in {
      authenticatedAs("jordan@demo.com.au", "mj-23") { (userUuid, token) =>
        requestWithPayload[UserUpdate](Put, route, token, s"/v1/secure/users/$userUuid", UserUpdate("Michael", "Jeffrey Jordan"), StatusCodes.OK)
      }
    }

    "be able to view another users profile" in {
      authenticatedAs("jordan@demo.com.au", "mj-23") { (userUuid, token) =>
        // Logged in as jordan trying to view scotties profile
        requestNoPayloadWithResponse[User](Get, route, token, s"/v1/secure/users/$pippensUuid") { user =>
          user.email should ===("pippen@demo.com.au")
        }
      }
    }

    "not be able to update his another user's profile" in {
      authenticatedAs("jordan@demo.com.au", "mj-23") { (userUuid, token) =>
        // logged in as jordan trying to update pippens profile
        requestWithPayload[UserUpdate](Put, route, token, s"/v1/secure/users/$pippensUuid", UserUpdate("Michael", "Jeffrey Jordan"), StatusCodes.Forbidden)
      }
    }

    "not be able to perform an action with an invalid token" in {
      authenticatedAs("jordan@demo.com.au", "mj-23") { (userUuid, token) =>
        requestWithPayload[UserUpdate](Put, route, "invalid-token", s"/v1/secure/users/$userUuid", UserUpdate("M", "J"), StatusCodes.Unauthorized)
      }
    }
  }
}
