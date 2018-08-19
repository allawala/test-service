package allawala.demo.core.module

import allawala.chassis.core.module.ChassisModule
import allawala.demo.auth.module.TestAuthModule
import allawala.demo.user.module.UserModule

class TestModule extends ChassisModule {
  override def configure(): Unit = {
    // IMPORTANT!!! always call super.configure
    super.configure()

    install(new UserModule)
    install(new TestAuthModule)
  }
}
