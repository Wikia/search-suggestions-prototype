package com.wikia.search.demo.wiki

case class Article(
                    var wikiId: Int,
                    id:Int,
                    title:String,
                    namespace:Int,
                    url:String,
                    `abstract`:String = null,
                    thumbnail: String = null,
                    var redirects:List[String] = List(),
                    var backlinkCount:Int = 0
                    )