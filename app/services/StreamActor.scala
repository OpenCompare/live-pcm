package services

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, Props}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import play.api.libs.json.Json
import services.StreamActor.TopHashtag
import twitter4j.Status

/**
  * Created by gbecan on 13/06/16.
  */
class StreamActor[T] (val out : ActorRef, val stream : DStream[T]) extends Actor {

  stream.foreachRDD { rdd =>
    rdd.take(5).foreach { element =>
      out ! element.toString
    }
  }

  override def receive: Receive = {
    case msg: String => out ! "Ok"
    case TopHashtag(top) => out ! Json.toJson(top)
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {

  }
}

object StreamActor {
  def props[T](out: ActorRef, stream : DStream[T]) = Props(new StreamActor(out, stream))

  case class TopHashtag(top : Map[String, Int])
}