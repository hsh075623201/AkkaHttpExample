import akka.actor.{Props, ActorSystem}
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{ExceptionHandler, RouteResult}
import akka.http.scaladsl.server.RouteResult.Complete
import akka.http.scaladsl.server.directives.{LogEntry, LoggingMagnet, DebuggingDirectives}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import utils.CommonUtil
import scala.concurrent.Future
import scala.io.StdIn
import StatusCodes._

/**
  * Created by admin on 2017/12/1.
  */
object Boot extends App with AkkaHttpExampleService{

  implicit val system = ActorSystem("AkkaHttpExampleSystem")
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
          complete(HttpResponse(InternalServerError, entity = "Error:"+e.toString))
        }
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
