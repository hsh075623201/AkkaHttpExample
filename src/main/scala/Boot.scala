import akka.actor.{Props, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import scala.io.StdIn

/**
  * Created by admin on 2017/12/1.
  */
object Boot extends App with AkkaHttpExampleService{

  implicit val system = ActorSystem("AkkaHttpExampleSystem")
  implicit val materializer = ActorMaterializer()

  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val interface = Config().getString("server.interface")
  val port = Config().getInt("server.port")
  val bindingFuture = Http().bindAndHandle(AkkaHttpExampleRoutes, interface, port)

  println(s"Server online at http://"+interface+":"+port+"/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
