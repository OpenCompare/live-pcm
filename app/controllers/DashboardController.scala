package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.Materializer
import org.apache.spark.streaming.Seconds
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import services.{SparkService, StreamActor}

@Singleton()
class DashboardController @Inject() ()(implicit system: ActorSystem, materializer: Materializer, configuration : Configuration, lifecycle: ApplicationLifecycle) extends Controller {

  val sparkService = SparkService.getInstance(configuration, lifecycle)

  def index = Action {
    Ok(views.html.index())
  }

  def stream = WebSocket.accept[String, String] { request =>
    val filters = Seq("Euro")
    val tweets = sparkService.getTwitterStream(filters)
    val stream = tweets
      //      .map { tweet =>
      //        tweet.getText
      //      }
      //      .flatMap { text => text.split("\\s") }
      //      .filter(_.startsWith("#"))
      .map { tweet =>
      Option(tweet.getPlace).map(_.getCountry)
    }
      .filter(_.isDefined)
      .map(_.get)
      .map { element => (element, 1)}
      .reduceByKeyAndWindow(_ + _, Seconds(60))
      .transform( count => count.sortBy(_._2, false))

    ActorFlow.actorRef(out => StreamActor.props(out, stream))
  }

}
