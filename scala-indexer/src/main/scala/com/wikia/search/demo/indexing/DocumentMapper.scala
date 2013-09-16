package com.wikia.search.demo.indexing

import com.wikia.search.demo.wiki.Article
import org.apache.solr.common.SolrInputDocument
import collection.JavaConversions._

class DocumentMapper {
  def map( article:Article ):SolrInputDocument = {
    val documentMap = new SolrInputDocument()
    documentMap.addField("id", article.wikiId + "_" + article.id )
    documentMap.addField("pageId_i", "" + article.id )
    documentMap.addField("wikiId_i", "" + article.wikiId )
    documentMap.addField("title_ngram", article.title )
    documentMap.addField("redirects_ngram_mv", seqAsJavaList(article.redirects) )
    documentMap.addField("abstract_en", article.`abstract` )
    documentMap.addField("title", article.title )
    documentMap.addField("pageUrl_url", article.url )
    documentMap.addField("namespace_i", "" + article.namespace )
    if( article.thumbnail != null ) { documentMap.addField("thumbnail_url", article.thumbnail ) }

    documentMap
  }
}
