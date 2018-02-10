package crdt.modules

import com.redis.RedisClientPool
import play.api._
import play.api.inject._

class RedisModule extends Module {

  override def bindings(env: Environment, config: Configuration): Seq[Binding[_]] = {

    Seq(
      bind[RedisClientPool].toProvider[RedisPoolProvider]
    )

  }

}
