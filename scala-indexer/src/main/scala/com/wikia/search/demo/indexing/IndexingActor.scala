package com.wikia.search.demo.indexing

import actors.Actor
import com.wikia.search.demo.wiki.Article
import org.apache.solr.client.solrj.request.UpdateRequest
import org.apache.solr.common.{SolrInputDocument, SolrDocument}
import org.apache.solr.client.solrj.SolrServer
import java.util
import collection.mutable.ListBuffer
import collection.JavaConversions._

class IndexingActor( val client:SolrServer ) extends Actor {
  val mapper = new DocumentMapper()

  def act() {
    loop {
      react {
        case x: List[Article] => {
          println( "indexing:" + x.length )
          client.request(getSolrRequest(x))
          client.commit(true, false)
          println("commit finished")
        }
        case None => exit()
      }
    }
  }

  def getSolrRequest(articles: List[Article]): UpdateRequest = {
    val request = new UpdateRequest()
    val docs:util.List[SolrInputDocument] = seqAsJavaList( articles.map( x => getSolrDocument(x) ) )
    request.add(docs)
    return request
  }

  def getSolrDocument( article:Article ):SolrInputDocument = {
    val m = mapper.map(article)
    return m
  }
}
