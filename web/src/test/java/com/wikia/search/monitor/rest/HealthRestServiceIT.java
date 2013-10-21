package com.wikia.search.monitor.rest;

import com.wikia.search.testing.TransientSolrFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrConfig;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HealthRestServiceIT {
    private static Logger logger = LoggerFactory.getLogger(HealthRestServiceIT.class);
    private static String endpointUrl;
    private SolrServer solrServer;

    @Before
    public void beforeClass() throws IOException, SolrServerException {
        endpointUrl = System.getProperty("service.url") + "api/health";
        logger.info(endpointUrl);
    }

    @Test
    public void testPing() throws Exception {
        WebClient client = WebClient.create(endpointUrl + "/ping");
        Response r = client.accept("application/json").get();
        logger.info(r.toString());
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
    }
}
