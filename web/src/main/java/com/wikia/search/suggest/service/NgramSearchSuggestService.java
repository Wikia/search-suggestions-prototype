package com.wikia.search.suggest.service;

import com.wikia.search.suggest.model.Suggestion;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.MessageFormat;
import java.util.List;

public class NgramSearchSuggestService implements SearchService {
    private static Logger logger = LoggerFactory.getLogger(NgramSearchSuggestService.class);

    @Autowired
    @Qualifier("solrSlave")
    private SolrServer solrServer;

    private int timeAllowed = 500;

    @Override
    public List<Suggestion> search( int wikiId, String query ) throws SearchException {
        try {
            query = ClientUtils.escapeQueryChars(query);
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setTimeAllowed( timeAllowed );
            solrQuery.setQuery("wikiId_i:" + wikiId);
            solrQuery.addFilterQuery("(title_ngram:" + query + ") OR (redirects_ngram_mv:" + query + ")");
            solrQuery.add("defType", "edismax");
            solrQuery.add("bq", "(title_simple:" + query + ")^10000000 (redirects_simple_mv:" + query + ")^10000");
            solrQuery.add("bf", "pow(views_i,0.5)^1 pow(backlinks_i,1.5)^10");
            QueryResponse response = solrServer.query(solrQuery);

            return response.getBeans(Suggestion.class);
        } catch (SolrServerException e) {
            logger.error(MessageFormat.format("Error while fetching results for {0},\"{1}\"", wikiId, query), e);
            throw new SearchException(e);
        }
    }
}
