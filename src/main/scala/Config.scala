import com.typesafe.config.ConfigFactory

/**
  * Created by admin on 2017/12/1.
  */
object Config {

  val env = if (System.getenv("SCALA_ENV") == null) "product" else System.getenv("SCALA_ENV")

  val conf = ConfigFactory.load()

  def apply() = conf.getConfig(env)

}
