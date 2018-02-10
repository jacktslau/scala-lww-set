package crdt.services
import crdt.{Element, LWWSet}

import scala.concurrent.Future

class MockLWWSetServiceImpl extends LWWSetService {
  protected def save(key: String, elem: Element[String]): Future[Boolean] = Future.successful {
    if(key == "exception") throw new RuntimeException("test")
    key != "fail"
  }

  override def add(key: String, elem: Element[String]): Future[Boolean] = save(key, elem)
  override def remove(key: String, elem: Element[String]): Future[Boolean] = save(key, elem)

  override def get(key: String): Future[Option[LWWSet[String]]] = Future.successful {
    key match {
      case "set" => Some(LWWSet().add(Element("test", 1)))
      case "empty" => Some(LWWSet())
      case _ => None
    }
  }
}
