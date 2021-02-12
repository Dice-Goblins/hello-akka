import actors.Guardian
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import routes.BasicRoutes
import utils.JsonSupport

import scala.io.StdIn

object Server extends App with JsonSupport {
    val system = ActorSystem[Nothing](Guardian(), "hello-akka")
}
