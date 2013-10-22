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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Search service. Performing search using fields:
 * <ul>
 *   <li>title_prefix_suffix</li>
 *   <li>redirects_prefix_suffix_mv</li>
 *   <li>title_simple - boost title exact match</li>
 *   <li>redirects_simple_mv - boost redirects exact match</li>
 *   <li>views_i - boosting only</li>
 *   <li>backlinks_i - boosting only</li>
 * </ul>
 */
public class SuffixSearchSuggestService implements SearchService {
    private static Logger logger = LoggerFactory.getLogger(SuffixSearchSuggestService.class);
    private SolrServer solrServer;
    private QuerySanitizer querySanitizer;
    private int timeAllowed = 500;

    @Autowired
    public SuffixSearchSuggestService(
            @Qualifier("solrSlave") SolrServer solrServer,
            @Qualifier("querySanitizer") QuerySanitizer querySanitizer) {
        this.solrServer = solrServer;
        this.querySanitizer = querySanitizer;
    }

    @Override
    public List<Suggestion> search( int wikiId, String query ) throws SearchException {
        try {
            query = querySanitizer.sanitize(query);
            if ( query.isEmpty() ) {
                // If query is empty we will create query that is not valid. i.e. (title_simple:)
                // There is no point of asking solr for such queries. We just return empty list.
                return new ArrayList<>();
            }
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setTimeAllowed( timeAllowed );
            solrQuery.setQuery("wikiId_i:" + wikiId);
            solrQuery.addFilterQuery("(title_prefix_suffix:" + query + "*) OR (redirects_prefix_suffix_mv:" + query + "*)");
            solrQuery.add("defType", "edismax");
            solrQuery.add("bq", "(title_simple:" + query + ")^10000000 (redirects_simple_mv:" + query + ")^5000000");
            solrQuery.add("bf", "pow(views_i,0.5)^1 pow(backlinks_i,1.5)^10");
            QueryResponse response = solrServer.query(solrQuery);

            return response.getBeans(Suggestion.class);
        } catch (SolrServerException e) {
            logger.error(MessageFormat.format("Error while fetching results for {0},\"{1}\"", wikiId, query), e);
            throw new SearchException(e);
        }
    }
}
