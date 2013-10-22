package com.wikia.search.testing;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import java.io.IOException;


public class TestingSolrServerFactory {

    public SolrServer get() throws IOException, SolrServerException {
        String endpointUrl = System.getProperty("solr.url");

        HttpSolrServer solrServer = new HttpSolrServer(endpointUrl);
        solrServer.ping();
        return solrServer;
    }

    public TestIndexingService getIndexingService() throws IOException, SolrServerException {
        return new TestIndexingService(get());
    }
}
