package com.wikia.search.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class SolrHealthCheck extends HealthCheck {
    private static Logger logger = LoggerFactory.getLogger(SolrHealthCheck.class);

    @Autowired
    @Qualifier("solrSlave")
    private SolrServer solrServer;

    @Override
    protected Result check() throws Exception {
        SolrPingResponse ping = solrServer.ping();
        if( ping.getStatus() == 0 ) {
            if ( ping.getElapsedTime() < 100 ) {
                return Result.healthy(String.format("Solr ping response time: %d", ping.getElapsedTime()));
            } else {
                String message = String.format("Solr ping response timeout: %d", ping.getElapsedTime());
                logger.warn("Unhealthy: " + message);
                return Result.unhealthy(message);
            }
        }else {
            String message = String.format("Solr ping response status: %d", ping.getStatus());
            logger.warn("Unhealthy: " + message);
            return Result.unhealthy(message);
        }
    }
}
