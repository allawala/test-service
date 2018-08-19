package allawala.demo.auth.module

import allawala.demo.auth.service.{EncryptionService, EncryptionServiceImpl}
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

class TestAuthModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[EncryptionService].to[EncryptionServiceImpl].asEagerSingleton()
  }
}
