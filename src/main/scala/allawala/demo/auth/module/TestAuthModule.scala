package allawala.demo.auth.module

import allawala.chassis.auth.module.AuthModule
import allawala.chassis.auth.service.TokenStorageService
import allawala.chassis.auth.shiro.module.ShiroAuthModule
import allawala.demo.auth.realm.InMemoryUserNamePasswordRealm
import allawala.demo.auth.service.{EncryptionService, EncryptionServiceImpl, TestCredentialsMatcher}
import allawala.demo.user.service.UserTokenServiceImpl
import com.google.inject.multibindings.Multibinder
import org.apache.shiro.realm.Realm

class TestAuthModule extends AuthModule {
  override def configure(): Unit = {
    super.configure()
    bind[EncryptionService].to[EncryptionServiceImpl].asEagerSingleton()
  }

  override protected def bindTokenStorageService(): Unit = {
    bind[TokenStorageService].to[UserTokenServiceImpl].asEagerSingleton()
  }
  /*
    You can also bind
    - custom Authorizers here
    - custom permission resolvers here
   */
  override protected def configureShiroModule(): Unit = {
    bind[TestCredentialsMatcher]

    install(new ShiroAuthModule {

      override protected def bindRealms(): Unit = {
        val multibinder = Multibinder.newSetBinder(binder, classOf[Realm])
//        multibinder.addBinding().to(classOf[JWTAuthorizingRealm])
        multibinder.addBinding().to(classOf[InMemoryUserNamePasswordRealm])
      }
    })
  }
}
