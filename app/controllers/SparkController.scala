package controllers

import javax.inject._

import org.apache.spark.streaming.Seconds
import play.api._
import play.api.inject.ApplicationLifecycle
import play.api.mvc._
import services.SparkService

@Singleton
class SparkController @Inject() (configuration : Configuration, lifecycle: ApplicationLifecycle) extends Controller {

  val sparkService = SparkService.getInstance(configuration, lifecycle)

  def start = Action {
    println("starting stream")
    sparkService.start()
    Ok("")
  }

  def stop = Action {
    println("stoping stream")
    sparkService.stop()
    Ok("")
  }

}
