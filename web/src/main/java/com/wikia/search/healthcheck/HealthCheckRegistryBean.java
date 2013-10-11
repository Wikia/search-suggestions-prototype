package com.wikia.search.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

public class HealthCheckRegistryBean implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(HealthCheckRegistryBean.class);
    private HealthCheckRegistry registry;
    private Map<String, HealthCheck> healthChecks;

    @Override
    public void afterPropertiesSet() throws Exception {
        for( Map.Entry<String, HealthCheck> entry: healthChecks.entrySet() ) {
            logger.info("Register health check:" + entry.getKey());
            registry.register(entry.getKey(), entry.getValue());
        }
    }

    public void setRegistry(HealthCheckRegistry registry) {
        this.registry = registry;
    }

    public void setHealthChecks(Map<String, HealthCheck> healthChecks) {
        this.healthChecks = healthChecks;
    }
}
