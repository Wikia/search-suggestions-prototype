package com.wikia.search.demo.wiki

import java.net.URLEncoder
import dispatch._
import org.slf4j.LoggerFactory
import net.liftweb.util.JSONParser._
import concurrent.ExecutionContext.Implicits.global
import net.liftweb.common.Full

case class BacklinksInfo( wikiId:Int, pageId:Int, backlinks:Int )
case class RedirectsInfo( wikiId:Int, pageId:Int, redirects:List[String] )
case class DetailsInfo( id:Int,
                        title: String,
                        url: String,
                        ns: Int,
                        `abstract`: String,
                        thumbnail: String ) {
  def toArticle:Article = {
    new Article(-1,id,title,ns,url,`abstract`, thumbnail )
  }
}
case class GetDetailsResponse(
                     items: Map[String, DetailsInfo],
                     basepath: String
                     )

class WikiApiClient( val apiUrl:String ) {
  val log = LoggerFactory.getLogger(classOf[WikiApiClient])

  def details( idList:List[Int], width: Int, height: Int, abstractSize: Int ):Future[Map[Int, DetailsInfo]]= {
    val requestUrlBase = "?controller=ArticlesApi&method=getDetails" +
      "&width=" + width +
      "&height=" + height +
      "&abstract=" + abstractSize

    val responseStringFuture = request( requestUrlBase, idList ,"," )
    responseStringFuture.map { (plainJson) =>

      implicit val formats = net.liftweb.json.DefaultFormats
      val json = net.liftweb.json.parse( plainJson )

      val getDetailsResponse:GetDetailsResponse = json.extract[GetDetailsResponse]
      getDetailsResponse.items.map( (x) => {
        (x._1.toInt, x._2)
      }).toMap
    }
  }

  def redirects( idList:List[Int] ):Future[Map[Int, RedirectsInfo]]= {
    val responseStringFuture = request("?controller=WikiaSearchIndexer&method=get&service=Redirects", idList)
    responseStringFuture.map { (plainJson) =>
      parseIndexerResponse( plainJson, (pageDescription:Map[String,Any]) => {
        val id = pageDescription("id").toString.split("_")(1).toInt
        val wikiId = pageDescription("id").toString.split("_")(0).toInt
        pageDescription.keys.filter( (key) => key.startsWith("redirect_titles_mv_") ).toList match {
          case List( key:String ) => {
            val redirects = pageDescription(key) match {
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

  def request( path:String, idList: List[Int], delimiter:String = "|"):Future[String] = {
    val ids: String = idList.map(x => x.toString).reduce((a, b) => a + delimiter + b)
    val requestUrl = apiUrl + path +
      "&ids=" + URLEncoder.encode( ids, "ASCII" )
    log.info( "request: " + requestUrl )
    val response = url(requestUrl)
    Http(response OK as.String)
  }
}
