package com.wikia.search.demo.wiki

import actors.Actor
import com.wikia.search.demo.wiki.ArticlesApi.GetDetails.Response
import concurrent.ExecutionContext.Implicits.global
import dispatch._
import java.net.URLEncoder
import net.liftweb.json._


class GetDetailsFilter( val apiUrl:String , val consumer:Actor ) extends Actor {
  val imgSize = 50
  val abstractSize = 100
  var noneReceived = false
  var waitingForResponse:Int = 0

  def act() {
    loop {
      react {
        case chunk: List[Article] => fetch( chunk )
        case None => { noneReceived = true; checkFinish() }
      }
    }
  }

  def fetch ( chunk: List[Article] ) {
    val ids:String = chunk.map( x => x.id.toString ).foldLeft("")( (a,b) => a+","+b )
    val requestUrl = apiUrl + "?controller=ArticlesApi&method=getDetails" +
      "&width=" + imgSize +
      "&height=" + imgSize +
      "abstract=" + abstractSize +
      "&ids=" + ids // URLEncoder.encode(ids, "utf-8")

    println(requestUrl)
    val response = url(requestUrl)
    val responseString = Http(response OK as.String)
    waitingForResponse += 1
    for ( plainJson <- responseString ) {
      waitingForResponse -= 1
      implicit val formats = net.liftweb.json.DefaultFormats
      val json = parse( plainJson )

      val articles = json.extract[Response]
      for ( value <- articles.items.values ) {
        val article = value.toArticle()
        article.wikiId = -1
        consumer ! article
      }
      checkFinish()
    }
  }

  def checkFinish() {
    if ( noneReceived && waitingForResponse == 0 ) finish()
  }

  def finish() {
    println("finish")
    consumer ! None
    exit()
  }
}
