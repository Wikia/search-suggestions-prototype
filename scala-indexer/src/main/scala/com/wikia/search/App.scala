package com.wikia.search

import demo.indexing.IndexingActor
import demo.util.AggregatorActor
import demo.wiki.{GetIndexerDataFilter, GetDetailsFilter, Article, WikiArticleListProducer}
import actors.Actor._
import java.net.URL
import org.apache.solr.client.solrj.impl.HttpSolrServer

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

  def main(args : Array[String]) {
    println( "Hello World!" )
    val apiUrl = args(0)

    val client = new HttpSolrServer("http://localhost:8983/solr/suggest")
    val indexer = new IndexingActor( client ).start()

    val preIndexerAggregate = new AggregatorActor[Article]( 120, indexer ).start()
    val getIndexerDataFilter = new GetIndexerDataFilter( apiUrl, preIndexerAggregate ).start()
    val getIndexerServiceAggregate = new AggregatorActor[Article]( 30, getIndexerDataFilter ).start()
    val getDetails = new GetDetailsFilter( apiUrl, getIndexerServiceAggregate ).start()
    val aggregate = new AggregatorActor[Article]( 120, getDetails ).start()
    new WikiArticleListProducer( apiUrl , aggregate ).start()
  }

}
