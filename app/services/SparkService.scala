package services

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
import twitter4j.{Status, TwitterFactory}
import twitter4j.auth.AccessToken

import scala.concurrent.Future

class SparkService private(configuration : Configuration, lifecycle: ApplicationLifecycle) {

  // Configure Spark
  val conf = new SparkConf()
    .setMaster("local[2]")
    .setAppName("LivePCM")

  // Create streaming context
  val ssc = new StreamingContext(conf, Seconds(1))

  lifecycle.addStopHook { () =>
    Future.successful(ssc.stop())
  }

  // Configure Twitter stream
  val consumerKey = configuration.getString("twitter4j.oauth.consumerKey").get
  val consumerSecret = configuration.getString("twitter4j.oauth.consumerSecret").get
  val accessToken = configuration.getString("twitter4j.oauth.accessToken").get
  val accessTokenSecret = configuration.getString("twitter4j.oauth.accessTokenSecret").get

  val twitter = TwitterFactory.getSingleton
  twitter.setOAuthConsumer(consumerKey, consumerSecret)
  twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret))


  /**
    * Create stream of tweets
    * @param filters keywords
    * @return stream of tweets
    */
  def getTwitterStream(filters: Seq[String]) : ReceiverInputDStream[Status] = {
    TwitterUtils.createStream(ssc, Some(twitter.getAuthorization), filters)
  }


  def start() = {
    ssc.start()
  }

  def stop() = {
    ssc.stop()
  }

}

object SparkService {

  private var instance : Option[SparkService] = None

  def getInstance(configuration : Configuration, lifecycle: ApplicationLifecycle) : SparkService = {
    if (instance.isEmpty) {
      instance = Some(new SparkService(configuration : Configuration, lifecycle: ApplicationLifecycle))
    }
    instance.get
  }

}