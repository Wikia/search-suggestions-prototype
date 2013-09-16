package com.wikia.search.demo.wiki

import actors.Actor
import concurrent.ExecutionContext.Implicits.global
import dispatch._
import java.net.URLEncoder
import net.liftweb.json._
import com.wikia.search.demo.wiki.WikiaSearchIndexer.Get.{ResponseContents, Command, Response}


class GetIndexerDataFilter( val apiUrl:String, val consumer:Actor ) extends Actor {
  var noneReceived = false
  var waitingForResponse:Int = 0
  def act() {
    println("act")
    loop {
      react {
        case chunk: List[Article] => fetch( chunk )
        case None => { noneReceived = true; checkFinish() }
        case _ => println("IndexerService got unknonw message.")
      }
    }
  }

  def fetch ( chunk: List[Article] ) {
    val articleMap:Map[Int, Article] = chunk map{ x => (x.id,x) } toMap
    val ids:String = chunk.map( x => x.id.toString ).reduce( (a,b) => a+"|"+b )
    val requestUrl = apiUrl + "?controller=WikiaSearchIndexer&method=get&service=Redirects" +
      "&ids=" + URLEncoder.encode(ids, "utf-8")

    println(requestUrl)
    val response = url(requestUrl)
    val responseString = Http(response OK as.String)
    waitingForResponse += 1
    for ( plainJson <- responseString ) {
      waitingForResponse -= 1
      println("done" + requestUrl)
      implicit val formats = net.liftweb.json.DefaultFormats
      val json = parse( plainJson )

      val articles = json.extract[Response]
      for ( commands <- articles.updateCommands() ) {
        commands.id match {
          case Some(fullid:String) => {
            val idParts:Array[String] = fullid.split("_")
            val pageId = idParts(1).toInt
            val wikiId = idParts(0).toInt
            println( (pageId,wikiId) )
            articleMap.get( pageId ) match {
              case Some(article) => { article.wikiId = wikiId; consumeArticle( article, commands ) }
              case None => println("no match for:" + pageId )
            }
          }
        }
      }
      checkFinish()
    }
  }

  def consumeArticle( article:Article, commands:ResponseContents ) = {
    println(article)
    val redirects:List[String] = commands.redirect_titles_mv_en match {
      case Some(x) => x.stringListValue()
      case None => List()
    }
    article.redirects = redirects
    consumer ! article
  }

  def checkFinish() {
    if ( noneReceived && waitingForResponse == 0 ) finish()
  }

  def finish() {
    println("finish indexerService")
    consumer ! None
    exit()
  }
}
