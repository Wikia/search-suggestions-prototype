package com.wikia.search

import demo.indexing.IndexingActor
import demo.util.AggregatorActor
import demo.wiki._
import demo.wiki.Article
import org.apache.solr.client.solrj.impl.HttpSolrServer
import akka.actor.{ActorRef, ActorSystem, Props, Actor}
import akka.contrib.throttle.TimerBasedThrottler
import akka.contrib.throttle.Throttler._
import concurrent.duration._
import akka.util._
import akka.contrib.throttle.Throttler.SetTarget
import scala.Some
import concurrent.Await

object App {

  /*
  val reader = actor {
    loop {
      react {
        case x: List[Article] => {
          println( x )
          println( x.length )
        }
        case x => println( x )
      }
    }
  }
  */
  val system = ActorSystem("IndexingSystem")

  def makeThrottle( nextActor: akka.actor.ActorRef ):ActorRef = {
    val throttler = system.actorOf(Props(classOf[TimerBasedThrottler], 10 msgsPer 1.second))
    throttler ! SetTarget(Some(nextActor))
    throttler
  }

  def makeThrottledActor( actor:Props, batchSize: Int ):ActorRef = {
    val consumer =  system.actorOf(actor)
    val throttle = makeThrottle( consumer )
    system.actorOf(Props( new AggregatorActor(batchSize, throttle) ))

  }

  def main(args : Array[String]) {
    val apiUrl = args(0)
    val api = new WikiApiClient( apiUrl )

    val client = new HttpSolrServer("http://localhost:8983/solr/suggest")

    val indexer =  makeThrottledActor(Props(new IndexingActor( client )), 300)
    val indexerServiceData =  makeThrottledActor(Props(new GetIndexerDataFilter( api, indexer )), 50)
    val getDetails = makeThrottledActor(Props(new GetDetailsFilter( api, indexerServiceData )), 200)

    new WikiArticleListProducer( apiUrl , getDetails ).start()

    system.awaitTermination()
  }

}
