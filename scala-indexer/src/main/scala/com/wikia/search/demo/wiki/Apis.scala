package com.wikia.search.demo.wiki.ArticlesApi.GetList {

import com.wikia.search.demo.wiki.Article

case class ArticleDto(
                       id: Int,
                       title: String,
                       url: String,
                       ns: Int
                       ) {
  def toArticle():Article = {
    new Article(-1,id,title,ns,url)
  }
}

case class Response (
                      items: List[ArticleDto],
                      offset: Option[String],
                      basepath: String
                      )
}
package com.wikia.search.demo.wiki.ArticlesApi.GetDetails {

import com.wikia.search.demo.wiki.Article

case class ArticleDto (
                        id: Int,
                        title: String,
                        url: String,
                        ns: Int,
                        `abstract`: String,
                        thumbnail: String
                        ) {
  def toArticle():Article = {
    return new Article(-1,id,title,ns,url,`abstract`, thumbnail )
  }
}
case class Response(
                     items: Map[String, ArticleDto],
                     basepath: String
                     )
}

package com.wikia.search.demo.wiki.WikiaSearchIndexer.Get {
  case class Response( contents:List[ResponseContents], errors:List[Any] ) {
    def updateCommands():List[ResponseContents] = {
      val a = contents.filter( x => x.delete == None ).toList
      return a
    }
  }

  case class ResponseContents( id:Option[String], redirect_titles_mv_en: Option[Command], delete: Option[Any] )

  case class Command( set:Option[List[String]] ) {
    def stringValue():String = {
      set match {
        //case Some(x:Int) => return x.toString
        //case Some(x:String) => return x
        case Some(head::tail) => return head.toString
        case Some(List()) => return ""
        case None => return ""
      }
    }

    def intValue():Int = {
      set match {
        //case Some(x:Int) => return x
        //case Some(x:String) => return x.toInt
        case Some(head::tail) => return head.toString.toInt
        case Some(List()) => return 0
        case None => return 0
      }
    }

    def stringListValue():List[String] = {
      println ( "stringListValue:" )
      println ( set )
      set match {
        case Some(x:List[String]) => return x
        //case Some(x:String) => return List(x)
        case None => List()
      }
    }
  }
}