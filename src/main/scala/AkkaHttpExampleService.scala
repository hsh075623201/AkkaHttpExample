
import java.util.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._

/**
  * Created by admin on 2017/12/1.
  */
trait AkkaHttpExampleService extends SprayJsonSupport with ExtendedJsonSupport{
  val AkkaHttpExampleRoutes = respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")){
    pathPrefix("hank"){
      (path("hello") & get){
        parameters("flag","option".?) {
          (flag,option)=>{
            println(option)
            if(flag=="1"){
              println("waiting...")
              Thread.sleep((Math.random()*1000).toInt+5000)
              println("sleeped...")
            }
            println("@@@@")
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, flag+"<h1>Say hello to akka-http</h1>"+new Date().getTime))
          }
        }
      }~(path("hello") & post){
       entity(as[Map[String,String]]){
         data=>{
           complete{
             data.getOrElse("flag","sdfsf").toString
           }
         }
       }
      }
    }
  }

}
