package crdt.modules

import com.google.inject.AbstractModule
import crdt.services.{LWWSetService, MockLWWSetServiceImpl}

class MockServiceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[LWWSetService])
      .to(classOf[MockLWWSetServiceImpl])
      .asEagerSingleton()
  }
}
