package com.wikia.search.solr;
/**
 * Author: Artur Dwornik
 * Date: 10.09.13
 * Time: 22:16
 */

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpProcessor;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class SolrConnectionFactory {
    private static Logger logger = LoggerFactory.getLogger(SolrConnectionFactory.class);
    private final List<HttpProcessor> httpProcessors;
    private String slaveUrl;
    private String masterUrl;

    public SolrConnectionFactory() {
        this(new ArrayList<HttpProcessor>());
    }

    @Autowired
    public SolrConnectionFactory(List<HttpProcessor> httpProcessors) {
        this.httpProcessors = httpProcessors;
    }

    public SolrServer getSlave() {
        logger.debug("Creating solr slave connection.");
        DefaultHttpClient httpClient = getDefaultHttpClient();
        return new HttpSolrServer(getSlaveUrl(), httpClient);
    }

    public SolrServer getMaster() {
        logger.debug("Creating solr master connection.");
        DefaultHttpClient httpClient = getDefaultHttpClient();
        return new HttpSolrServer(getMasterUrl(), httpClient);
    }

    private DefaultHttpClient getDefaultHttpClient() {
        // consider making this a factory.
        DefaultHttpClient httpClient = new DefaultHttpClient();
        // register http interceptors
        for( HttpProcessor httpProcessor:httpProcessors ) {
            httpClient.addRequestInterceptor(httpProcessor);
            httpClient.addResponseInterceptor(httpProcessor);
        }
        return httpClient;
    }

    public String getSlaveUrl() {
        return slaveUrl;
    }

    public void setSlaveUrl(String slaveUrl) {
        logger.info("Setting solr slave to: " + slaveUrl);
        this.slaveUrl = slaveUrl;
    }

    public String getMasterUrl() {
        return masterUrl;
    }

    public void setMasterUrl(String masterUrl) {
        logger.info("Setting solr master to: " + masterUrl);
        this.masterUrl = masterUrl;
    }
}
