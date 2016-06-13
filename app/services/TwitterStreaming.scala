package services

import javax.inject.{Inject, Singleton}

import org.apache.spark.streaming.Seconds
import play.api.Configuration
import play.api.inject.ApplicationLifecycle

@Singleton
class TwitterStreaming @Inject() (configuration : Configuration, lifecycle: ApplicationLifecycle) {

  val sparkService = SparkService.getInstance(configuration, lifecycle)


  def startStream() {
    val filters = Seq("Euro")
    val tweets = sparkService.getTwitterStream(filters)
    tweets
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
      .foreachRDD { rdd =>
        rdd.take(5).foreach { status =>
          println(status)
        }
      }
    println("starting stream")
    sparkService.start()
  }

  def stopStream() {
    println("stoping stream")
    sparkService.stop()
  }

}