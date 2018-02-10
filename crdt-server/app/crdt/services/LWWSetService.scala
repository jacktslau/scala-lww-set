package crdt.services

import crdt.{Element, LWWSet}

import scala.concurrent.Future

trait LWWSetService {

  /** Add a new element into a LWW-Set `key`
    *
    * @param key  LWW-Set key
    * @param elem element to be saved
    * @return     a Boolean indicate the action is success or not
    */
  def add(key: String, elem: Element[String]): Future[Boolean]

  /** Remove an element in the LWW-Set `key`
    *
    * @param key  LWW-Set key
    * @param elem element to be removed
    * @return     a Boolean indicate the action is success or not
    */
  def remove(key: String, elem: Element[String]): Future[Boolean]

  /** Return the LWW-Set by the given `key`
    *
    * @param key LWW-Set key
    * @return    LWW-Set
    */
  def get(key: String): Future[Option[LWWSet[String]]]

}
