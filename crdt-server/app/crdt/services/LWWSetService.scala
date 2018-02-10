package crdt.services

import crdt.{Element, LWWSet}

import scala.concurrent.Future

trait LWWSetService {

  /** Add new elements into a LWW-Set `key`
    *
    * @param key   LWW-Set key
    * @param elems element to be saved
    * @return      number of records inserted or updated
    */
  def add(key: String, elems: List[Element[String]]): Future[Long]

  /** Remove elements in the LWW-Set `key`
    *
    * @param key   LWW-Set key
    * @param elems element to be removed
    * @return      number of records removed
    */
  def remove(key: String, elems: List[Element[String]]): Future[Long]

  /** Return the LWW-Set by the given `key`
    *
    * @param key LWW-Set key
    * @return    LWW-Set
    */
  def get(key: String): Future[Option[LWWSet[String]]]

}
