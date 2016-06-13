package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import services.{StreamActor, TwitterStreaming}

@Singleton()
class DashboardController @Inject() (val twitterStreaming: TwitterStreaming)(implicit system: ActorSystem, materializer: Materializer) extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def stream = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => StreamActor.props(out))
  }

}
