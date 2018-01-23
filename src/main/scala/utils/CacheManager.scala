package utils
import com.redis.RedisClientPool
import spray.json._

/**
  * Created by Hank on 12/21/2017..
  */
object CacheManager {

  val redisClientPool = "dev".equalsIgnoreCase(System.getenv("SCALA_ENV")) match {
    case true => new RedisClientPool(Config().getString("redis.host"), 6379)
    case false => new RedisClientPool(Config().getString("redis.host"), 6379, 8, 0, Some(Config().getString("redis.password")))
  }
  val redisDBNum = Config().getInt("redis.database")

  def getRedisKeyPrefix() ={
    Config().getString("redis.keyPrefix")
  }
  val redisKeyPrefix=getRedisKeyPrefix()//Config().getString("redis.keyPrefix")  //redis缓存key前缀

  def delCacheByKey(key:String) ={
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val result = client.del(key)
        println(result)
      }
    )
  }
  //批量删除
  def delBatchCacheByKey(keysList:List[String]) ={
    if(keysList.size>0){
      redisClientPool.withClient(
        client => {
          client.select(redisDBNum)
          val result = client.del(keysList(0),keysList.drop(1):_*)
          println(result)
        }
      )
    }
  }

  def getCacheByKey(key:String) ={
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val result = client.get(redisKeyPrefix+key)
        import DefaultJsonProtocol._
        result.getOrElse(Map[String,Set[String]]().toJson.toString())
      }
    )
  }
  //模糊查询key
  def getSimilarCacheKey(key:String) ={
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val result = client.keys(key).get.sorted.reverse
        result
      }
    )
  }

  def updateCache(key: String, value: String, expireTime: Int): String = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        client.set(key, value)
        if(expireTime != -1){
          client.expire(key, expireTime)
        }
        value
      }
    )
  }

  /**
    * 缓存无参数的方法
    *
    * @param f          获取数据的Method
    * @param name       redis中的Key，一般为该Method的名字
    * @param expireTime 过期时间（可不传，默认为300s）
    * @return
    */
  def fetchCache0(f: () => String, name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(), expireTime))
      }
    )
  }

  /**
    * 缓存含有一个参数的方法
    *
    * @param f          获取数据的Method
    * @param params     f的参数列表
    * @param name       redis中的Key，一般为该Method的名字
    * @param expireTime 过期时间（可不传，默认为300s）
    * @tparam P0 泛型
    * @return
    */
  def fetchCache1[P0](f: (P0) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0]
        ), expireTime))
      }
    )
  }

  def fetchCache2[P0, P1](f: (P0, P1) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1]
        ), expireTime))
      }
    )
  }

  def fetchCache3[P0, P1, P2](f: (P0, P1, P2) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse({
          updateCache(key, f(
            params.head.asInstanceOf[P0],
            params(1).asInstanceOf[P1],
            params(2).asInstanceOf[P2]
          ), expireTime)
        })

      }
    )
  }

  def fetchCache4[P0, P1, P2, P3](f: (P0, P1, P2, P3) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3]
        ), expireTime))
      }
    )
  }

  def fetchCache5[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4]
        ), expireTime))
      }
    )
  }

  def fetchCache6[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5]
        ), expireTime))
      }
    )
  }

  def fetchCache7[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6]
        ), expireTime))
      }
    )
  }

  def fetchCache8[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6],
          params(7).asInstanceOf[P7]
        ), expireTime))
      }
    )
  }

  def fetchCache9[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6],
          params(7).asInstanceOf[P7],
          params(8).asInstanceOf[P8]
        ), expireTime))
      }
    )
  }

  def fetchCache10[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6],
          params(7).asInstanceOf[P7],
          params(8).asInstanceOf[P8],
          params(9).asInstanceOf[P9]
        ), expireTime))
      }
    )
  }

  def fetchCache11[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6],
          params(7).asInstanceOf[P7],
          params(8).asInstanceOf[P8],
          params(9).asInstanceOf[P9],
          params(10).asInstanceOf[P10]
        ), expireTime))
      }
    )
  }

  def fetchCache12[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6],
          params(7).asInstanceOf[P7],
          params(8).asInstanceOf[P8],
          params(9).asInstanceOf[P9],
          params(10).asInstanceOf[P10],
          params(11).asInstanceOf[P11]
        ), expireTime))
      }
    )
  }

  def fetchCache13[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11,P12](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11,P12) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6],
          params(7).asInstanceOf[P7],
          params(8).asInstanceOf[P8],
          params(9).asInstanceOf[P9],
          params(10).asInstanceOf[P10],
          params(11).asInstanceOf[P11],
          params(12).asInstanceOf[P12]
        ), expireTime))
      }
    )
  }

  def fetchCache14[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11,P12, P13](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11,P12, P13) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6],
          params(7).asInstanceOf[P7],
          params(8).asInstanceOf[P8],
          params(9).asInstanceOf[P9],
          params(10).asInstanceOf[P10],
          params(11).asInstanceOf[P11],
          params(12).asInstanceOf[P12],
          params(13).asInstanceOf[P13]
        ), expireTime))
      }
    )
  }
  def fetchCache15[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11,P12, P13,P14](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11,P12, P13,P14) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6],
          params(7).asInstanceOf[P7],
          params(8).asInstanceOf[P8],
          params(9).asInstanceOf[P9],
          params(10).asInstanceOf[P10],
          params(11).asInstanceOf[P11],
          params(12).asInstanceOf[P12],
          params(13).asInstanceOf[P13],
          params(14).asInstanceOf[P14]
        ), expireTime))
      }
    )
  }

  def fetchCache18[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11,P12, P13, P14, P15, P16, P17](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11,P12, P13, P14, P15, P16, P17) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6],
          params(7).asInstanceOf[P7],
          params(8).asInstanceOf[P8],
          params(9).asInstanceOf[P9],
          params(10).asInstanceOf[P10],
          params(11).asInstanceOf[P11],
          params(12).asInstanceOf[P12],
          params(13).asInstanceOf[P13],
          params(14).asInstanceOf[P14],
          params(15).asInstanceOf[P15],
          params(16).asInstanceOf[P16],
          params(17).asInstanceOf[P17]
        ), expireTime))
      }
    )
  }

  def fetchCache19[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11,P12, P13, P14, P15, P16, P17,P18](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11,P12, P13, P14, P15, P16, P17,P18) => String, params: List[Any], name: String, expireTime: Int = 300) = {
    redisClientPool.withClient(
      client => {
        client.select(redisDBNum)
        val key = redisKeyPrefix+name + ":" + params.mkString(":")
        val result = client.get(key)

        result.getOrElse(updateCache(key, f(
          params.head.asInstanceOf[P0],
          params(1).asInstanceOf[P1],
          params(2).asInstanceOf[P2],
          params(3).asInstanceOf[P3],
          params(4).asInstanceOf[P4],
          params(5).asInstanceOf[P5],
          params(6).asInstanceOf[P6],
          params(7).asInstanceOf[P7],
          params(8).asInstanceOf[P8],
          params(9).asInstanceOf[P9],
          params(10).asInstanceOf[P10],
          params(11).asInstanceOf[P11],
          params(12).asInstanceOf[P12],
          params(13).asInstanceOf[P13],
          params(14).asInstanceOf[P14],
          params(15).asInstanceOf[P15],
          params(16).asInstanceOf[P16],
          params(17).asInstanceOf[P17],
          params(18).asInstanceOf[P18]
        ), expireTime))
      }
    )
  }

}