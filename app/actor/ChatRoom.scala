package actor

import akka.actor.Actor
import play.api.libs.iteratee.{Enumerator, Iteratee, Concurrent}
import actor.Messages.{Leave, Broadcast}
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

class ChatRoom extends Actor {

  private var users = Set[String]()

  private val (enum, channel) = Concurrent.broadcast[String]

  def receive: Actor.Receive = {
    case actor.Messages.Join(name) => {
      println(s"user: $name joined.")
      if (!users.contains(name)) {
        val iteratee = Iteratee.foreach[String] {
          msg =>
            self ! Broadcast(s"$name : $msg ")
        }.map {
          _ => self ! Leave(name)
        }

        users += name
        channel.push(s"user: $name joined.\r\n")
        sender !(iteratee, enum)
      }
      else {
        val e = Enumerator("Nick name $name is already exist")
        val i = Iteratee.ignore

        sender !(e, i)
      }

    }

    case actor.Messages.Leave(name) => {
      users = users filter (u => u != name)
      channel.push(s"User:$name left chat. $users.size remained.")
    }

    case actor.Messages.Broadcast(msg) => {
      println(msg)
      channel.push(msg)
    }
  }
}
