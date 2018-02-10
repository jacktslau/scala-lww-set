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

  override def add(key: String, elems: List[Element[String]]): Future[Long] = save(s"$key-addset", elems)

  override def remove(key: String, elems: List[Element[String]]): Future[Long] = save(s"$key-removeset", elems)

  protected def save(member: String, elems: List[Element[String]]): Future[Long] = {
    if(elems.size <= 0) Future.successful(0L)
    else {
      logger.debug(s"Saving $elems to $member")
      for {
        result <- redis.withClient { cli =>
          Future {
            val scoreVals = elems.map(e => (e.ts.toDouble, e.value))
            if (scoreVals.size > 1) {
              val score = scoreVals.head._1
              val value = scoreVals.head._2
              cli.zadd(member, score, value, scoreVals.drop(1):_*)

            } else {
              val score = scoreVals.head._1
              val value = scoreVals.head._2
              cli.zadd(member, score, value)
            }

          }
        }
      } yield result.getOrElse(0L) // TODO insert error handling
    }
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
