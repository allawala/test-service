package allawala.common

import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import allawala.chassis.config.model.LanguageConfig
import allawala.chassis.http.route.{HasRoute, RouteWrapper}
import allawala.chassis.i18n.service.I18nService
import allawala.chassis.util.DataGenerator
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.scalatest._
import org.slf4j.MDC

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.reflect.ClassTag

abstract class IntegrationSpec[T <: HasRoute : Manifest] extends WordSpecLike
  with Matchers
  with ScalatestRouteTest
  with RouteWrapper {
  type JWTToken = String

  val timeout: FiniteDuration = 60 seconds
  private val ValidStatuses: Set[StatusCode] = Set(StatusCodes.OK, StatusCodes.Created)
  protected val dataGenerator: DataGenerator = GuiceUtil.instance[DataGenerator]

  override def i18nService: I18nService = new I18nService(GuiceUtil.instance[LanguageConfig])

  def waitASec(): Unit = Thread.sleep(1000)

  protected val route: Route = {
    Route.seal(
      handleExceptions(routesExceptionHandler) {
        handleRejections(routesRejectionHandler) {
          GuiceUtil.instance[T].route
        }
      }
    )
  }

  protected def unauthenticatedRequest[Req: Encoder, Resp: Decoder : ClassTag](
                                                                                httpRequestBuilder: RequestBuilder,
                                                                                route: Route,
                                                                                url: String,
                                                                                requestModel: Req
                                                                              )(
                                                                                f: Resp => Assertion
                                                                              ): Assertion = {

    MDC.put("X-CORRELATION-ID", dataGenerator.uuidStr)
    httpRequestBuilder.apply(
      url, HttpEntity(`application/json`, requestModel.asJson.noSpaces)
    ) ~> route ~> check {
      f(entityAs[Resp])
    }
  }

  protected def requestWithPayload[Req: Encoder](
                                                  httpRequestBuilder: RequestBuilder,
                                                  route: Route,
                                                  token: JWTToken,
                                                  url: String,
                                                  requestModel: Req,
                                                  status: StatusCode
                                                ): Assertion = {

    MDC.put("X-CORRELATION-ID", dataGenerator.uuidStr)
    httpRequestBuilder.apply(
      url, HttpEntity(`application/json`, requestModel.asJson.noSpaces)
    ) ~> addHeader("Authorization", token) ~> route ~> check {
      // entity needs to be consumed
      if (response.status != status) {
        logger.error(s"*** ${httpRequestBuilder.method} to $url failed ***")
      }
      response.discardEntityBytes()
      response.status should ===(status)
    }
  }

  protected def requestNoPayload(
                                  httpRequestBuilder: RequestBuilder,
                                  route: Route,
                                  token: JWTToken,
                                  url: String,
                                  status: StatusCode
                                ): Assertion = {

    MDC.put("X-CORRELATION-ID", dataGenerator.uuidStr)
    httpRequestBuilder.apply(url) ~> addHeader("Authorization", token) ~> route ~> check {
      // entity needs to be consumed
      if (response.status != status) {
        logger.error(s"*** ${httpRequestBuilder.method} to $url failed ***")
      }
      response.discardEntityBytes()
      response.status should ===(status)
    }
  }

  protected def requestNoPayloadWithResponse[Resp: Decoder : ClassTag](
                                                                        httpRequestBuilder: RequestBuilder,
                                                                        route: Route,
                                                                        token: JWTToken,
                                                                        url: String
                                                                      )
                                                                      (
                                                                        f: Resp => Assertion
                                                                      ): Assertion = {


    MDC.put("X-CORRELATION-ID", dataGenerator.uuidStr)
    httpRequestBuilder.apply(url) ~> addHeader("Authorization", token) ~> route ~> check {
      f(entityAs[Resp])
      ValidStatuses.contains(response.status) shouldBe true
    }
  }
}
