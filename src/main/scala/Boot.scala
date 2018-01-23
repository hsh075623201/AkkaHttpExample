import akka.actor.{ActorSystem, Props}
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{ExceptionHandler, RouteResult}
import akka.http.scaladsl.server.RouteResult.Complete
import akka.http.scaladsl.server.directives.{LogEntry, LoggingMagnet, DebuggingDirectives}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.aihuishou.hbase.Utils.CommonUtil
import com.aihuishou.utils.CommonUtil
import com.typesafe.config.ConfigFactory
import utils.CommonUtil
import scala.concurrent.Future
import scala.io.StdIn
import StatusCodes._

/**
  * Created by admin on 2017/12/1.
  */
object Boot extends App with AkkaHttpExampleService{
  //不同开发环境 设置不同的日志级别 by hank
  val logConf = System.getenv("SCALA_ENV") match {
    case null=>ConfigFactory.parseString("""akka.loglevel="INFO"""")//生产环境
    case _=>ConfigFactory.parseString("""akka.loglevel="INFO"""")//开发环境
  }
  implicit val system = ActorSystem("AkkaHttpExampleSystem",logConf.withFallback(ConfigFactory.load()))
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  //
  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: Exception =>
        extractUri { uri =>
          val dateTime = CommonUtil.getCurrentDateTime
          System.err.println(s"[ERROR] [$dateTime] URL: $uri")
          e.printStackTrace()
          complete(CommonUtil.warpResponse(e.toString,StatusCodes.InternalServerError.intValue,StatusCodes.InternalServerError.defaultMessage))        }
    }

  val interface = Config().getString("server.interface")
  val port = Config().getInt("server.port")

  // logs just the request method and response status at info level
  def requestMethodAndResponseStatusAsInfo(req: HttpRequest): RouteResult => Option[LogEntry] = {
    case RouteResult.Complete(res) => Some(LogEntry(
      "Method:"+req.method.name+
      "\nURL:"+req.uri +
      "\n"+req.headers.head +
      "\nbody:"+req.entity +
      "\nstatus:" + res.status+
      "\nresult:"+res.entity,
      Logging.InfoLevel))
    case _                         => None // no log entries for rejections
  }
  val clientRouteLogged = logRequestResult(requestMethodAndResponseStatusAsInfo _)(AkkaHttpExampleRoutes)

  val bindingFuture = Http().bindAndHandle(clientRouteLogged, interface, port)

  println(s"Server online at http://"+interface+":"+port+"/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
