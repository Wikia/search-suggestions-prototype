package com.wikia.search.http;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;

/**
 * This is required by MetricsServlet to find appropriate metrics registry.
 */
public class MetricsServletContextListener extends MetricsServlet.ContextListener {
    private static Logger  logger = LoggerFactory.getLogger(MetricsServletContextListener.class);
    @Autowired()
    private MetricRegistry metricRegistry;

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
    protected MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

}
