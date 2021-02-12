package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.collection.immutable

object CommandProcessor {
  final case class User(name: String, age: Int)
  final case class Users(users: immutable.Seq[User])

  sealed trait Command
  final case class GetUsers(replyTo: ActorRef[Users]) extends Command
  final case class CreateUser(user: User, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetUser(name: String, replyTo: ActorRef[GetUserResponse]) extends Command
  final case class DeleteUser(name: String, replyTo: ActorRef[ActionPerformed]) extends Command

  sealed trait Response
  final case class GetUserResponse(maybeUser: Option[User]) extends Response
  final case class ActionPerformed(description: String) extends Response

  def apply(): Behavior[Command] = setup

  private def setup: Behavior[Command] = {
    Behaviors.setup { context =>
      context.log.info("User registry starting up.")
      processor(Set.empty)
    }
  }

  private def processor(users: Set[User]): Behavior[Command] = {
    Behaviors.receiveMessage {
      case GetUsers(replyTo) =>
          replyTo ! Users(users.toSeq)
          Behaviors.same
      case CreateUser(user, replyTo) =>
          replyTo ! ActionPerformed(s"User ${user.name} created")
          processor(users + user)
      case GetUser(name, replyTo) =>
        replyTo ! GetUserResponse(users.find(_.name == name))
        Behaviors.same
      case DeleteUser(name, replyTo) =>
        replyTo ! ActionPerformed(s"User $name deleted")
        processor(users.filterNot(_.name == name))
    }
  }

}
