package crdt.modules

import com.google.inject.AbstractModule
import crdt.services.{LWWSetService, LWWSetServiceRedisImpl}

class ServiceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[LWWSetService])
      .to(classOf[LWWSetServiceRedisImpl])
      .asEagerSingleton()
  }
}
