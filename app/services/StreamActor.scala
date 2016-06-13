package services

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, Props}

/**
  * Created by gbecan on 13/06/16.
  */
class StreamActor (val out : ActorRef) extends Actor {
  override def receive: Receive = {
    case msg: String => out ! "Ok"
  }
}

object StreamActor {
  def props(out: ActorRef) = Props(new StreamActor(out))
}