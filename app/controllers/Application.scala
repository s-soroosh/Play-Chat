package controllers

import play.api._
import akka.pattern.ask
import play.api.mvc._
import play.api.libs.iteratee._
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.concurrent.Promise
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import akka.util.Timeout
import play.libs.Akka
import akka.actor.Props
import actor.ChatRoom
import actor.Messages.Join

object Application extends Controller {
  implicit val timeout = Timeout(1 seconds)
  val room = Akka.system.actorOf(Props[ChatRoom])

  def chatSocket(nick: String) = WebSocket.async { request =>

    val channelsFuture = room ? Join(nick)
    channelsFuture.mapTo[(Iteratee[String,_], Enumerator[String])]
  }



  def i = WebSocket.using[String] {
    implicit request =>
      val in = Iteratee.ignore[String]
      val out = Enumerator.repeatM {
        println("dada")
        Promise.timeout("1", 3 seconds)
      }
      (in, out)
  }


  def index = Action {
    implicit request =>
      Ok(views.html.index("Hoho"))
  }


}