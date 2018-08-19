package allawala.demo.core

import allawala.chassis.core.Microservice
import allawala.demo.core.module.TestModule

object TestApp extends Microservice with App {
  override def module: TestModule = new TestModule

  run
}
