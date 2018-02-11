package crdt.modules

import javax.inject.{Inject, Provider, Singleton}

import com.redis.RedisClientPool
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Logger}

import scala.concurrent.Future

@Singleton
class RedisPoolProvider @Inject()(config: Configuration, lifecycle: ApplicationLifecycle) extends Provider[RedisClientPool]{

  lazy val logger = Logger(classOf[RedisPoolProvider])

  lazy val get: RedisClientPool = {
    val redisPool = {
      val host = config.getOptional[String]("redis.host").getOrElse("localhost")
      val port = config.getOptional[Int]("redis.port").getOrElse(6379)
      val secret = config.getOptional[String]("redis.secret")
      val maxIdle = config.getOptional[Int]("redis.maxIdle").getOrElse(10)
      val database = config.getOptional[Int]("redis.database").getOrElse(0)
      val timeout = config.getOptional[Int]("redis.timeout").getOrElse(3000)
      val maxConnections = config.getOptional[Int]("redis.maxConnections").getOrElse(100)
      val poolWaitTimeout = config.getOptional[Long]("redis.poolWaitTimeout").getOrElse(3000L)

      new RedisClientPool(host, port, maxIdle, database, secret, timeout, maxConnections, poolWaitTimeout = poolWaitTimeout)
    }

    logger.info(s"Creating Redis Pool: $redisPool")

    lifecycle.addStopHook(() => Future.successful {
      logger.info("Stopping Redis Pool")
      redisPool.close
    })

    redisPool
  }

}
