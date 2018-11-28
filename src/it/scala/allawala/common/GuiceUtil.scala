package allawala.common

import allawala.demo.core.module.TestModule
import com.google.inject._
import net.codingwell.scalaguice.InjectorExtensions._

object GuiceUtil {
  private val injector: Injector = Guice.createInjector(Stage.PRODUCTION, new TestModule {
    // Override any configuration as needed
  })

  def instance[U: Manifest]: U = injector.instance[U]
}
