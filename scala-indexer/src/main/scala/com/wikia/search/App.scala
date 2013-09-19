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
  val system = ActorSystem("HelloSystem")

  def makeThrottle( nextActor: akka.actor.ActorRef ):ActorRef = {
    val throttler = system.actorOf(Props(classOf[TimerBasedThrottler], 1 msgsPer 1.second))
    throttler ! SetTarget(Some(nextActor))
    throttler
  }

  def main(args : Array[String]) {
    val apiUrl = args(0)
    val api = new WikiApiClient( apiUrl );

    val client = new HttpSolrServer("http://localhost:8983/solr/suggest")
    val indexer = system.actorOf( Props(new IndexingActor( client )) )

    val preIndexerAggregate =  system.actorOf(Props(new AggregatorActor[Article]( 120, indexer )))
    val getIndexerDataFilter = system.actorOf(Props(new GetIndexerDataFilter( api, preIndexerAggregate )))
    val throttle1 = makeThrottle( getIndexerDataFilter )
    val getIndexerServiceAggregate = system.actorOf(Props(new AggregatorActor[Article]( 120, throttle1 )))
    val getDetails = system.actorOf(Props(new GetDetailsFilter( apiUrl, getIndexerServiceAggregate )))
    val throttle2 = makeThrottle( getDetails )
    val aggregate = system.actorOf(Props(new AggregatorActor[Article]( 120, throttle2 )))
    new WikiArticleListProducer( apiUrl , aggregate ).start()
    system.awaitTermination()
  }

}
