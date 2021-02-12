package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import routes.{ApiV1, BasicRoutes}

import scala.util.{Failure, Success}

object Guardian {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    // TODO: Get from config
    val port = 8080

    val futureBinding = Http().newServerAt("0.0.0.0", port).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
          val address = binding.localAddress
          system.log.info("Server online at httop://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
          system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
          system.terminate()
    }
  }

  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Nothing] { context =>
      context.log.info("Guardian Actor Starting")
      val commandProcessor = context.spawn(CommandProcessor(), "CommandProcessor")

      val routes = new ApiV1(commandProcessor)(context.system)
      context.log.info("Starting HTTP Server")
      startHttpServer(routes.routes)(context.system)

      context.log.info("Guardian Started")

      Behaviors.empty
    }
  }

}
