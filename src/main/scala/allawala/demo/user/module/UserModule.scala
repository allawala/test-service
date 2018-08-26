package allawala.demo.user.module

import allawala.demo.user.repository.{UserRepository, UserRepositoryImpl, UserTokenRepository, UserTokenRepositoryImpl}
import allawala.demo.user.route.{UserPublicRoute, UserRoute}
import allawala.demo.user.service.{UserService, UserServiceImpl}
import allawala.demo.user.translation.{UserTranslator, UserTranslatorImpl}
import allawala.demo.user.validation.{UserValidator, UserValidatorImpl}
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

class UserModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[UserPublicRoute].asEagerSingleton()
    bind[UserRoute].asEagerSingleton()
    bind[UserService].to[UserServiceImpl].asEagerSingleton()
    bind[UserValidator].to[UserValidatorImpl].asEagerSingleton()
    bind[UserTranslator].to[UserTranslatorImpl].asEagerSingleton()
    bind[UserRepository].to[UserRepositoryImpl].asEagerSingleton()
    bind[UserTokenRepository].to[UserTokenRepositoryImpl].asEagerSingleton()
  }
}
