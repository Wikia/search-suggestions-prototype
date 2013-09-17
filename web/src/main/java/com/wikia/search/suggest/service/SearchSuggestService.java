package com.wikia.search.suggest.service;

import com.wikia.search.suggest.model.Suggestion;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class SearchSuggestService implements SearchService {
    private static Logger logger = LoggerFactory.getLogger(SearchSuggestService.class);

    @Autowired
    @Qualifier("solrSlave")
    private SolrServer solrServer;

    @Override
    public List<Suggestion> search( int wikiId, String query ) {
        try {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery("wikiId_i:" + wikiId);
            solrQuery.addFilterQuery("title_ngram:\"" + query + "\"");
            QueryResponse response = solrServer.query(solrQuery);
            return response.getBeans(Suggestion.class);
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        }
    }
}
