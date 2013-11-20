package actor

object Messages {

  case class Join(name: String)
  case class Leave(name: String)
  case class Broadcast(msg: String)

}