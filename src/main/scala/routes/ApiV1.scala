package routes

import actors.CommandProcessor
import actors.CommandProcessor._
import akka.http.scaladsl.server.Directives._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import io.circe.generic.auto._
import io.circe._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.concurrent.Future

class ApiV1(userRegistry: ActorRef[CommandProcessor.Command])(implicit val system: ActorSystem[_]) {

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("http.ask-timeout"))
  implicit val ec = system.executionContext

  def getUsers(): Future[Users] =
    userRegistry.ask(GetUsers)
  def getUser(name: String): Future[GetUserResponse] =
    userRegistry.ask(GetUser(name, _))
  def createUser(user: User): Future[ActionPerformed] =
    userRegistry.ask(CreateUser(user, _))
  def deleteUser(name: String): Future[ActionPerformed] =
    userRegistry.ask(DeleteUser(name, _))

  val routes: Route = pathPrefix("api" / "v1"){
    concat(
      userRoutes ~ healthRoutes
    )
  }

  private val healthRoutes: Route =
    path("health") {
      concat(
        pathEnd {
          concat(
            get {
              complete("ok")
            }
          )
        }
      )
    }

  private val userRoutes: Route = {
    pathPrefix("users") {
      concat(
        pathEnd {
          concat(
            get {
              complete(ActionPerformed("ok"))
            },
            post {
              entity(as[User]) { user =>
                onSuccess(createUser(user)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        path(Segment) { name =>
          concat(
            get {
              rejectEmptyResponse {
                onSuccess(getUser(name)) { response =>
                  complete(response.maybeUser)
                }
              }
            },
            delete {
              onSuccess(deleteUser(name)) { performed =>
                complete((StatusCodes.OK, performed))
              }
            }
          )
        }
      )
    }
  }
}
