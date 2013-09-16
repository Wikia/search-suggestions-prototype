package com.wikia.search.demo.wiki

case class Article(
                    var wikiId: Int,
                    id:Int,
                    title:String,
                    namespace:Int,
                    url:String,
                    `abstract`:String = null,
                    thumbnail: String = null,
                    var redirects:List[String] = List()
                    ) {
  def redirectsString:String = {
    val content = redirects.map( x => '"' + x + '"').reduceOption( (a,b) => a+","+b ) match {
      case None => ""
      case x => x
    }
    return "[" + content + "]"
  }
}
