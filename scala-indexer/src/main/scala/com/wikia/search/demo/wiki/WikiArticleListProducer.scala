package com.wikia.search.demo.wiki

import actors.Actor
import actors.Actor._
import com.wikia.search.demo.wiki.ArticlesApi.GetList.Response
import concurrent.ExecutionContext.Implicits.global
import dispatch._
import net.liftweb.json._
import java.net.URLEncoder


class WikiArticleListProducer( val apiUrl: String, val consumer: Actor ) {
  val limit = 500

  def start() = {
    processor ! ""
  }

  private val processor:Actor = actor {
    loop {
      react {
        case offset:String => {
          val requestUrl = apiUrl + "?controller=ArticlesApi&method=getList" +
            "&offset=" + URLEncoder.encode(offset, "utf-8") +
            "&limit=" + limit
          println(requestUrl)
          val response = url(requestUrl)
          val responseString = Http(response OK as.String)
          for ( plainJson <- responseString ) {
            implicit val formats = net.liftweb.json.DefaultFormats
            val json = parse( plainJson )
            val articles = json.extract[Response]
            for ( article <- articles.items ) {
              consumer ! article.toArticle()
            }
            articles.offset match {
              case Some(x) => { processor ! x }
              case None => { consumer ! None; /* exit() */ }
            }
          }
        }
      }
    }
  }
}
