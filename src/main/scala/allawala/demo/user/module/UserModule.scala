package allawala.demo.user.module

import allawala.demo.user.route.UserPublicRoute
import allawala.demo.user.service.{UserService, UserServiceImpl}
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

class UserModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[UserPublicRoute].asEagerSingleton()
    bind[UserService].to[UserServiceImpl].asEagerSingleton()
  }
}
