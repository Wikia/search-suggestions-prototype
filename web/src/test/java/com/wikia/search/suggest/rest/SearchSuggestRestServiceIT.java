package com.wikia.search.suggest.rest;

import com.google.common.collect.Lists;
import com.wikia.search.testing.TestIndexingService;
import com.wikia.search.testing.TestingSolrServerFactory;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class SearchSuggestRestServiceIT {
    private static final Logger logger = LoggerFactory.getLogger(SearchSuggestRestServiceIT.class);
    private String endpointUrl;

    @Before
    public void beforeClass() throws IOException, SolrServerException {
        endpointUrl = System.getProperty("service.url") + "api/search-suggest";
        logger.info(endpointUrl);

        TestIndexingService indexingService = new TestingSolrServerFactory().getIndexingService();
        indexingService.addDocument(1234, 1, 0, "John Price", "...", "http://img.com/img/img.jpg", 100, 100, Lists.newArrayList("John", "Price"));
        indexingService.addDocument(1234, 2, 0, "Spider-Man", "...", "http://img.com/img/img.jpg", 100, 100, Lists.newArrayList("Spider"));
        indexingService.addDocument(1234, 6, 0, "John (\"Soap\") MacTavish", "Captain John \"Soap\" MacTavish was the Scottish[4] main protagonist", "http://img.com/jm.jpg", 500, 100, Lists.newArrayList("Soap", "MacTavish"));
        indexingService.addDocument(1234, 8, 0, "John McCain", "...", "http://img.com/jm.jpg", 10, 100, new ArrayList<String>());
        indexingService.addDocument(1239, 13, 0, "John Price", "...", "http://img.com/img.jpg", 100000, 100, Lists.newArrayList("John", "Price"));
        indexingService.commit();
    }

    @Test
    public void testEmptyQuery() throws Exception {
        WebClient client = WebClient.create(endpointUrl + "?wikiId=3125");
        Response r = client.accept("application/json").get();
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        String value = IOUtils.toString((InputStream) r.getEntity());
        assertEquals("[]", value);
    }

    @Test
    public void testJohnPrice() throws Exception {
        WebClient client = WebClient.create(endpointUrl + "?wikiId=1234&q=John");
        Response r = client.accept("application/json").get();
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        String value = IOUtils.toString((InputStream) r.getEntity());

        with(value).assertThat("$[0].id", equalTo(1));
        with(value).assertThat("$[0].title", equalTo("John Price"));

        with(value).assertThat("$[1].id", equalTo(6));
        with(value).assertThat("$[1].title", equalTo("John (\"Soap\") MacTavish"));
    }


    @Test
    public void testJohnMacTavish() throws Exception {
        WebClient client = WebClient.create(endpointUrl + "?wikiId=1234&q=Jo");
        Response r = client.accept("application/json").get();
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        String value = IOUtils.toString((InputStream) r.getEntity());

        with(value).assertThat("$[0].id", equalTo(6));
        with(value).assertThat("$[0].title", equalTo("John (\"Soap\") MacTavish"));

        with(value).assertThat("$[1].id", equalTo(1));
        with(value).assertThat("$[1].title", equalTo("John Price"));
    }
}
