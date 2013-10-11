package com.wikia.search.http;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;

public class HealthCheckServletContextListener extends HealthCheckServlet.ContextListener {
    private static Logger logger = LoggerFactory.getLogger(HealthCheckServletContextListener.class);
    @Autowired()
    private HealthCheckRegistry healthCheckRegistry;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Force autowire.");
        WebApplicationContextUtils
                .getRequiredWebApplicationContext(sce.getServletContext())
                .getAutowireCapableBeanFactory()
                .autowireBean(this);
        super.contextInitialized(sce);
    }

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }
}
