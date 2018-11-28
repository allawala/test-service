package allawala

import allawala.chassis.core.exception.InitializationException
import allawala.common.GuiceUtil
import allawala.demo.user.service.UserServiceImpl
import allawala.user.UserSpec
import org.apache.shiro.SecurityUtils
import org.apache.shiro.mgt.SecurityManager
import org.scalatest.{BeforeAndAfterAll, Suites}

import scala.concurrent.Await
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps

/*
  IMPORTANT!!! role spec should always be run last as it modifies the state of the roles which can lead to other tests failing if the cache contains the role in an invalid
  state for the test
 */
class IntegrationSuite extends Suites(
  new UserSpec
) with BeforeAndAfterAll {

  val timeout: FiniteDuration = 60 seconds

  override protected def beforeAll(): Unit = {
    // initialize resources here once that are needed to run all the integration specs

    // IMPORTANT!!!
    SecurityUtils.setSecurityManager(GuiceUtil.instance[SecurityManager])

    // we will just create the admin here
    onSuccess(Await.result(GuiceUtil.instance[UserServiceImpl].preStart(), timeout))
  }

  override protected def afterAll(): Unit = {
    // clean up resources here
  }

  private def onSuccess(f: => Either[InitializationException, Unit]) = {
    f match {
      case Left(e) => throw e
      case Right(_) => // Do nothing
    }
  }
}
