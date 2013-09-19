package com.wikia.search.demo.indexing

import akka.actor.{ActorLogging, Actor}
import com.wikia.search.demo.wiki.Article
import org.apache.solr.client.solrj.request.UpdateRequest
import org.apache.solr.common.{SolrInputDocument, SolrDocument}
import org.apache.solr.client.solrj.SolrServer
import java.util
import collection.mutable.ListBuffer
import collection.JavaConversions._
import org.slf4j.LoggerFactory

class IndexingActor( val client:SolrServer ) extends Actor {
  val log = LoggerFactory.getLogger(classOf[IndexingActor])
  val mapper = new DocumentMapper()

  def receive = {
    case x: List[Article] => {
      log.info( "indexing:" + x.length )
      client.request(getSolrRequest(x))
      client.commit(true, false)
      log.info("commit finished")
    }
    case None => { log.info("stop indexing"); }
  }

  def getSolrRequest(articles: List[Article]): UpdateRequest = {
    val request = new UpdateRequest()
    val docs:util.List[SolrInputDocument] = seqAsJavaList( articles.map( x => mapper.map(x) ) )
    request.add(docs)
    request
  }
}
