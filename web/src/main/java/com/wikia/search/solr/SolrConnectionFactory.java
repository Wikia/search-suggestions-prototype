package com.wikia.search.solr;
/**
 * Author: Artur Dwornik
 * Date: 10.09.13
 * Time: 22:16
 */

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrConnectionFactory {
    private static Logger logger = LoggerFactory.getLogger(SolrConnectionFactory.class);

    public SolrServer getSlave() {
        logger.debug("Creating solr slave connection.");
        return new HttpSolrServer("http://localhost:8983/solr/suggest");
    }

    public SolrServer getMaster() {
        logger.debug("Creating solr master connection.");
        return new HttpSolrServer("http://localhost:8983/solr/suggest");
    }
}
