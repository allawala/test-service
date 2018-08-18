package allawala.test.core

import allawala.chassis.core.Microservice
import allawala.test.core.module.TestModule

object TestApp extends Microservice with App {
  override def module: TestModule = new TestModule

  run
}
