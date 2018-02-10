package crdt.services
import javax.inject.Inject

import com.redis.RedisClientPool
import crdt.{Element, LWWSet}
import play.api.Logger

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * LWW-Element-Set Service implementation backed by Redis Datastore
  * @param redis redis client pool
  */
class LWWSetServiceRedisImpl @Inject() (redis: RedisClientPool) extends LWWSetService {

  lazy val logger = Logger(getClass)

  // Redis Sorted Set to Element Transformation
  protected def zset2Element: (String, Double) => Element[String] = { (value, score) => Element(value, score.toLong) }

  override def add(key: String, elem: Element[String]): Future[Boolean] = save(s"$key-addset", elem.ts, elem.value)

  override def remove(key: String, elem: Element[String]): Future[Boolean] = save(s"$key-removeset", elem.ts, elem.value)

  protected def save(member: String, source: Double, value: String): Future[Boolean] = {
    logger.debug(s"Saving $value to $member with source $source")
    for {
      success <- redis.withClient { cli =>
        Future {
          cli.zadd(member, source, value)
        }
      }
    } yield success.isDefined
  }

  override def get(key: String): Future[Option[LWWSet[String]]] = {
    logger.debug(s"Getting lww-set $key")
    redis.withClient { cli =>
      for {
        asListOpt <- Future {
          cli.zrangeWithScore(s"$key-addset")
        }
        rsListOpt <- Future {
          cli.zrangeWithScore(s"$key-removeset")
        }
      } yield {
        asListOpt.map { asList =>
          val addSet = asList.map { case (k, v) => zset2Element(k, v) }.toSet
          val removeSet = rsListOpt.getOrElse(List.empty).map { case (k, v) => zset2Element(k, v) }.toSet

          LWWSet(addSet, removeSet)
        }
      }

    }
  }
}
