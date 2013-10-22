package com.wikia.search.suggest.service;

import com.wikia.search.suggest.model.Suggestion;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SuffixSearchSuggestServiceTest {

    @Test
    public void testSearchEmpty() throws Exception {
        SolrServer solrServerMock =  mock(SolrServer.class);
        QuerySanitizer querySanitizer =  mock(QuerySanitizer.class);
        when(querySanitizer.sanitize("]")).thenReturn("");

        SuffixSearchSuggestService suffixSearchSuggestService = new SuffixSearchSuggestService(solrServerMock,querySanitizer);
        List<Suggestion> suggestions = suffixSearchSuggestService.search(13, "]");

        assertThat(suggestions).isEmpty();
    }


    @Test
    public void testSearch() throws Exception {
        List<Suggestion> expectedSuggestions = mock(ArrayList.class);
        QueryResponse queryResponse = mock(QueryResponse.class);
        when(queryResponse.getBeans(Suggestion.class)).thenReturn(expectedSuggestions);
        SolrServer solrServerMock =  mock(SolrServer.class);
        when(solrServerMock.query(any(SolrQuery.class))).thenReturn(queryResponse);
        QuerySanitizer querySanitizer =  mock(QuerySanitizer.class);
        when(querySanitizer.sanitize("foo")).thenReturn("asd");

        SuffixSearchSuggestService suffixSearchSuggestService = new SuffixSearchSuggestService(solrServerMock,querySanitizer);
        List<Suggestion> result = suffixSearchSuggestService.search(13, "foo");

        assertThat(result).isEqualTo(expectedSuggestions);
    }
}
