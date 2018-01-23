
import java.util.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import com.aihuishou.common.ExtendedJsonSupport
import com.aihuishou.hbase.hbaseRoute
import com.aihuishou.utils.CommonUtil
import hbase.hbaseRoute

/**
  * Created by admin on 2017/12/1.
  */
trait AkkaHttpExampleService extends SprayJsonSupport with ExtendedJsonSupport with hbaseRoute{
  val AkkaHttpExampleRoutes = respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")){
    pathPrefix("hank"){
      (path("hello") & get){
        parameters("flag","option") {
          (flag,option)=>{
            var w = 1000+(Math.random()*500).toInt

            if(option.toInt % 7 ==0){
              w+=((Math.random()*1000).toInt+1500)
            }


            if(option=="10000"){
              Thread.sleep(100)
              complete("aaaaa")
            }else{
              Thread.sleep(w)
              complete(CommonUtil.warpResponse(w.toString))
            }
          }
        }
      }~(path("hello") & post){
       entity(as[List[Map[String,String]]]){
         data=>{
           complete{
             data.map(_.getOrElse("flag","sdfsf")).mkString(",")
           }
         }
       }
      }~HBaseRouteReport
    }
  }

}
