package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import services.TwitterStreaming

@Singleton
class SparkController @Inject() (val twitterStreaming: TwitterStreaming) extends Controller {


  def start = Action {
    twitterStreaming.startStream()
    Ok("")
  }

  def stop = Action {
    twitterStreaming.stopStream()
    Ok("")
  }

}
