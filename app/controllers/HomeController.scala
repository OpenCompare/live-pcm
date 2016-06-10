package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import services.TwitterStreaming

@Singleton
class HomeController @Inject() extends Controller {


  def index = Action {
    TwitterStreaming.startStream()
    Ok(views.html.index())
  }

}
