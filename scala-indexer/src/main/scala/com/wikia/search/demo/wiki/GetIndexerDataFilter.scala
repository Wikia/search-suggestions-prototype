package com.wikia.search.demo.wiki

import akka.actor.{Actor,ActorRef}
import concurrent.ExecutionContext.Implicits.global
import dispatch._
import java.net.URLEncoder
import net.liftweb.json._
import com.wikia.search.demo.wiki.WikiaSearchIndexer.Get.{ResponseContents, Command, Response}


class GetIndexerDataFilter( val api:WikiApiClient, val consumer:ActorRef ) extends Actor {

  def receive = {
    case chunk: List[Article] => fetch( chunk )
    case _ => println("IndexerService got unknonw message.")
  }

  def fetch ( chunk: List[Article] ) {
    val articleMap:Map[Int, Article] = chunk.map{ x => (x.id,x) } toMap

    val backlinksFuture = api.backlinks(articleMap.keys.toList)
    val redirectsFuture = api.redirects(articleMap.keys.toList)

    for( backlinks <- backlinksFuture;
         redirects <- redirectsFuture ) yield {
      val ids = articleMap.keySet.intersect(backlinks.keySet).intersect(redirects.keySet)
      for ( pageId <- ids ) {
        val backlink = backlinks.get(pageId).get
        val redirect = redirects.get(pageId).get
        articleMap.get( pageId ) match {
          case Some(article:Article) => {
            article.wikiId = backlink.wikiId
            article.backlinkCount = backlink.backlinks
            article.redirects = redirect.redirects
            consumer ! article
          }
        }
      }
    }
  }
}
