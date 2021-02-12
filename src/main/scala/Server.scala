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
    implicit val system = ActorSystem(Behaviors.empty, "hello-akka")
    implicit val executionContext = system.executionContext
    val config = ConfigFactory.load()
    val logger = LoggerFactory.getLogger(getClass)

    val bindingFuture = Http().newServerAt(config.getString("http.interface"), config.getInt("http.port")).bind(BasicRoutes.route)

    // TODO: Add Logging system
    logger.info("Server online at http://localhost:8080")

    // Block until key press
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}
