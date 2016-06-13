package services

import javax.inject.{Inject, Singleton}

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import play.api.Configuration
import twitter4j.auth.AccessToken
import twitter4j.{Status, TwitterFactory}

@Singleton()
class TwitterStreaming @Inject() (val configuration : Configuration) {

  val consumerKey = configuration.getString("twitter4j.oauth.consumerKey").get
  val consumerSecret = configuration.getString("twitter4j.oauth.consumerSecret").get
  val accessToken = configuration.getString("twitter4j.oauth.accessToken").get
  val accessTokenSecret = configuration.getString("twitter4j.oauth.accessTokenSecret").get


  val conf = new SparkConf()
    .setMaster("local[2]")
    .setAppName("LivePCM")

  val ssc = new StreamingContext(conf, Seconds(1))




  val twitter = TwitterFactory.getSingleton
  twitter.setOAuthConsumer(consumerKey, consumerSecret)
  twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret))

  val filters = Seq("Euro")

  var tweets : ReceiverInputDStream[Status] = _

  def startStream() {
    tweets = TwitterUtils.createStream(ssc, Some(twitter.getAuthorization), filters)
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
    ssc.start()
  }

  def stopStream() {
    println("stoping stream")
    ssc.stop()
  }

}
