package services

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j.Status

object TwitterStreaming {

  val conf = new SparkConf()
    .setMaster("local[2]")
    .setAppName("LivePCM")
  val ssc = new StreamingContext(conf, Seconds(1))
  val filters = Seq("Trump", "Hillary")
  var twitterStream : ReceiverInputDStream[Status] = _

  def startStream() {
    twitterStream = TwitterUtils.createStream(ssc, None, filters)
    twitterStream.map(status => println(status))
    println("starting stream")
    twitterStream.start()
  }

  def stopStream() {
    twitterStream.stop()
  }

}
