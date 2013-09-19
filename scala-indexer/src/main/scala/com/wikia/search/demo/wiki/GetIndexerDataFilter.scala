package com.wikia.search.demo.wiki

import akka.actor.{ActorLogging, Actor, ActorRef}
import concurrent.ExecutionContext.Implicits.global
import dispatch._
import java.net.URLEncoder
import net.liftweb.json._
import com.wikia.search.demo.wiki.WikiaSearchIndexer.Get.{ResponseContents, Command, Response}


class GetIndexerDataFilter( val api:WikiApiClient, val consumer:ActorRef ) extends Actor with ActorLogging {

  def receive = {
    case chunk: List[Article] => fetch( chunk )
    case _ => log.error("IndexerService got unknown message.")
  }

  def fetch ( chunk: List[Article] ) {
    val articleMap:Map[Int, Article] = chunk.map({ x => (x.id,x) }).toMap

    val backlinksFuture = api.backlinks(articleMap.keys.toList)
    val redirectsFuture = api.redirects(articleMap.keys.toList)
    val metadataFuture = api.getMetadata(articleMap.keys.toList)

    for( backlinks <- backlinksFuture;
         redirects <- redirectsFuture;
         metadataSet  <- metadataFuture ) yield {
      val ids = articleMap.keySet
        .intersect(backlinks.keySet)
        .intersect(redirects.keySet)
        .intersect(metadataSet.keySet)
      for ( pageId <- ids ) {
        val backlink = backlinks.get(pageId).get
        val redirect = redirects.get(pageId).get
        val metadata = metadataSet.get(pageId).get
        articleMap.get( pageId ) match {
          case Some(article:Article) => {
            article.wikiId = backlink.wikiId
            article.backlinkCount = backlink.backlinks
            article.redirects = redirect.redirects
            article.views = metadata.views
            consumer ! article
          }
        }
      }
    }
  }
}
