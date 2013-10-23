package com.wikia.search.solr;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.wikia.search.monitor.slowqueries.HttpRequestMeasurement;
import com.wikia.search.monitor.slowqueries.MonitorService;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class HttpMonitoringProcessor implements HttpProcessor {
    private static Logger logger = LoggerFactory.getLogger(HttpMonitoringProcessor.class);
    private final Timer httpRequestTimer;
    private final MonitorService monitorService;

    @Autowired
    public HttpMonitoringProcessor(MetricRegistry registry, MonitorService monitorService) {
        this.monitorService = monitorService;
        httpRequestTimer = registry.timer(MetricRegistry.name(HttpMonitoringProcessor.class.getSimpleName(), "httpRequestTimer"));
    }

    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
        httpContext.setAttribute("request line", httpRequest.getRequestLine());
        httpContext.setAttribute("http timer context", httpRequestTimer.time());
    }

    @Override
    public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        Timer.Context timerContext = (Timer.Context) httpContext.getAttribute("http timer context");
        RequestLine line = (RequestLine) httpContext.getAttribute("request line");
        if( timerContext == null ) {
            logger.warn("Null timer context.");
        } else {
            long stop = timerContext.stop();
            double millisecond = stop / 1000000.0d;
            monitorService.monitorHttpRequest(new HttpRequestMeasurement(line.getUri(), millisecond, DateTime.now(DateTimeZone.UTC)));
        }
    }
}
