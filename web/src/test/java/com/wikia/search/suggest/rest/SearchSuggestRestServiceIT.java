package com.wikia.search.suggest.rest;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class SearchSuggestRestServiceIT {
    private static final Logger logger = LoggerFactory.getLogger(SearchSuggestRestServiceIT.class);
    private static String endpointUrl;

    @BeforeClass
    public static void beforeClass() {
        endpointUrl = System.getProperty("service.url") + "api/search-suggest";
        logger.info(endpointUrl);
    }

    @Test
    public void testEmptyQuery() throws Exception {
        WebClient client = WebClient.create(endpointUrl + "?wikiId=3125");
        Response r = client.accept("application/json").get();
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        String value = IOUtils.toString((InputStream) r.getEntity());
        assertEquals("[]", value);
    }
}
