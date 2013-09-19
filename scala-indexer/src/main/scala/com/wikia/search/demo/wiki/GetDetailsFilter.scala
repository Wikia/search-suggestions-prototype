package com.wikia.search.demo.wiki

import akka.actor.{Actor,ActorRef}
import concurrent.ExecutionContext.Implicits.global


class GetDetailsFilter( val api:WikiApiClient , val consumer:ActorRef ) extends Actor {
  val imgSize = 50
  val abstractSize = 100

  def receive = {
    case chunk: List[Article] => fetch( chunk )
  }

  def fetch ( chunk: List[Article] ) {
    val ids:List[Int] = chunk.map( x => x.id )

    for ( detailsSet <- api.details( ids, imgSize, imgSize, abstractSize ) ) yield {
      for ( articleDetails:DetailsInfo <- detailsSet.values ) {
        val article:Article = articleDetails.toArticle
        consumer ! article
      }
    }
  }
}
