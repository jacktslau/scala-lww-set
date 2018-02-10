package crdt.config

import com.typesafe.config.Config
import play.api.ConfigLoader

case class RedisConfig (

)

object RedisConfigLoader extends ConfigLoader[RedisConfig] {

  override def load(config: Config, path: String): RedisConfig = {
    RedisConfig()
  }

}
