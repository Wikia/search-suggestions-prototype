package com.wikia.search.demo.wiki

import java.net.URLEncoder
import dispatch.{as, url}
import dispatch._
import org.slf4j.{LoggerFactory, Logger}
import net.liftweb.util.JSONParser._
import net.liftweb.json.JsonAST.{JArray, JObject}
import concurrent.ExecutionContext.Implicits.global
import net.liftweb.common.Full

case class BacklinksInfo( wikiId:Int, pageId:Int, backlinks:Int )
case class RedirectsInfo( wikiId:Int, pageId:Int, redirects:List[String] )

class WikiApiClient( val apiUrl:String ) {
  val log = LoggerFactory.getLogger(classOf[WikiApiClient])

  def redirects( idList:List[Int] ):Future[Map[Int, RedirectsInfo]]= {
    val responseStringFuture = request("?controller=WikiaSearchIndexer&method=get&service=Redirects", idList)
    responseStringFuture.map { (plainJson) =>
      parseIndexerResponse( plainJson, (pageDesription:Map[String,Any]) => {
        val id = pageDesription("id").toString.split("_")(1).toInt
        val wikiId = pageDesription("id").toString.split("_")(0).toInt
        pageDesription.keys.filter( (key) => key.startsWith("redirect_titles_mv_") ).toList match {
          case List( key:String ) => {
            val redirects = pageDesription(key) match {
              case y:Map[String, Any] => y("set") match {
                case redirects:List[String] => redirects
              }
            }
            (id, RedirectsInfo(wikiId,id,redirects))
          }
        }
      } )
    }
  }

  def backlinks( idList:List[Int] ):Future[Map[Int, BacklinksInfo]]= {
    val responseStringFuture = request("?controller=WikiaSearchIndexer&method=get&service=BacklinkCount", idList)
    responseStringFuture.map { (plainJson) =>
      parseIndexerResponse( plainJson, (x:Map[String,Any]) => {
        val id = x("id").toString.split("_")(1).toInt
        val wikiId = x("id").toString.split("_")(0).toInt
        val backlinks = x("backlinks") match {
          case y:Map[String, Any] => y("set") match {
            case backlinkCount:Int => backlinkCount
            case backlinkCount:Double => backlinkCount.toInt
          }
        }
        (id, BacklinksInfo(wikiId,id,backlinks))
      } )
    }
  }

  def parseIndexerResponse[T]( plainJson:String, mapper:(Map[String, Any]) => (Int,T) ):Map[Int,T] = {
    implicit val formats = net.liftweb.json.DefaultFormats
    val json = parse( plainJson )

    json match {
      case Full(obj:Map[String, Any]) => obj.get("contents") match {
        case Some(arr:List[Any]) => arr.filter({
          x => x match {
            case x:Map[String, Any] => x.keySet.contains("id")
          }
        }).map({
          xx => xx match {
            case x:Map[String, Any] => {
              mapper(x)
            }
          }
        }).toMap
      }
    }
  }

  def request( path:String, idList: List[Int]):Future[String] = {
    val ids: String = idList.map(x => x.toString).reduce((a, b) => a + "|" + b)
    val requestUrl = apiUrl + path +
      "&ids=" + URLEncoder.encode( ids, "ASCII" )
    val response = url(requestUrl)
    Http(response OK as.String)
  }
}
