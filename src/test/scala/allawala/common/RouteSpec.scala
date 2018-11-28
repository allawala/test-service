package allawala.common

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import allawala.chassis.auth.model.{JWTSubject, PrincipalType}
import allawala.chassis.auth.shiro.model.{AuthenticatedSubject, Principal}
import allawala.chassis.auth.shiro.service.ShiroAuthService
import allawala.chassis.http.route.RouteWrapper
import allawala.chassis.i18n.service.I18nService
import allawala.demo.user.model.User
import allawala.demo.user.service.UserService
import io.circe.Encoder
import io.circe.syntax._
import org.apache.shiro.subject.Subject
import org.scalatest.Assertion

import scala.collection.immutable
import scala.concurrent.Future

trait RouteSpec extends BaseSpec with ScalatestRouteTest with RouteWrapper {

  type JWTToken = String
  protected val userUuid = "user-uuid"
  private val userJwtToken: JWTToken = "jwtToken"

  override val i18nService: I18nService = mock[I18nService]
  protected val authService: ShiroAuthService = mock[ShiroAuthService]
  protected val userService: UserService = mock[UserService]

  private val subject = mock[Subject]

  protected val currentUser = User(
    uuid = userUuid,
    firstName = "current",
    lastName = "user",
    email = "currentuser@beamwallet.com"
  )

  private val principalUser = Principal(PrincipalType.User, userUuid)

  def mocksToReset: Seq[AnyRef] = Nil

  before {
    val mocks = Seq(authService, userService) ++ mocksToReset
    reset(mocks: _*)
  }

  trait AuthenticatedUser {
    implicit val jwtToken: JWTToken = userJwtToken
    implicit val jwtSubject: JWTSubject = JWTSubject(PrincipalType.User, userUuid, userJwtToken)
    authService.authenticateToken(
      equ(jwtToken), any[Option[String]]
    ) returns Future.successful(Right(AuthenticatedSubject(subject, jwtToken, None)))
    subject.getPrincipal returns principalUser
    userService.getUser(any[String]) returns Future.successful(Right(Some(currentUser)))

  }

  def assertSecurePost[Req: Encoder, Resp: Encoder](
                                                     route: Route, url: String, requestModel: Req,
                                                     responseStatus: StatusCode, responseModel: Resp,
                                                     headers: immutable.Seq[HttpHeader] = Nil
                                                   )(
                                                     implicit token: JWTToken
                                                   ): Assertion = {
    assertReqResp(
      Post, route, url, Some(requestModel.asJson.noSpaces), responseStatus, ContentTypes.`application/json`, responseModel.asJson.noSpaces, headers
    )
  }

  def assertSecurePut[Req: Encoder, Resp: Encoder](
                                                    route: Route, url: String, requestModel: Req,
                                                    responseStatus: StatusCode, responseModel: Resp,
                                                    headers: immutable.Seq[HttpHeader] = Nil
                                                  )(
                                                    implicit token: JWTToken
                                                  ): Assertion = {
    assertReqResp(
      Put, route, url, Some(requestModel.asJson.noSpaces), responseStatus, ContentTypes.`application/json`, responseModel.asJson.noSpaces, headers
    )
  }

  def assertSecureGet[Resp: Encoder](
                                      route: Route, url: String, responseStatus: StatusCode,
                                      responseModel: Resp, headers: immutable.Seq[HttpHeader] = Nil
                                    )(
                                      implicit token: JWTToken
                                    ): Assertion = {
    assertReqResp(Get, route, url, None, responseStatus, ContentTypes.`application/json`, responseModel.asJson.noSpaces, headers)
  }

  //noinspection ScalaStyle
  def assertReqResp(
                     httpRequestBuilder: RequestBuilder,
                     route: Route,
                     url: String,
                     requestModel: Option[String],
                     responseStatus: StatusCode,
                     contentType: ContentType,
                     responseModel: String,
                     headers: immutable.Seq[HttpHeader]
                   )(
                     implicit token: JWTToken
                   ): Assertion = {
    val httpRequest = if (requestModel.isDefined) {
      httpRequestBuilder.apply(url, HttpEntity(contentType, requestModel.get.getBytes()))
    } else {
      httpRequestBuilder.apply(url)
    }
    val routeTestResult = httpRequest ~> addHeader("Authorization", s"Bearer $token") ~> route

    val assertion = routeTestResult ~> check {
      response shouldEqual HttpResponse(
        status = responseStatus,
        entity = HttpEntity(
          contentType, responseModel.getBytes),
        headers = headers
      )
    }

    // ensures authentication calls happen (i.e the route is secure)
    oneOf(authService).authenticateToken(any[String], any[Option[String]])

    assertion
  }
}
