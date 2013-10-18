package com.wikia.search.monitor.rest;

import com.wikia.search.monitor.*;
import com.wikia.search.monitor.healthcheck.HealthCheckResult;
import com.wikia.search.monitor.healthcheck.HealthCheckService;
import com.wikia.search.monitor.slowqueries.HttpRequestMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("health")
public class HealthRestService {
    private static Logger logger = LoggerFactory.getLogger(HealthRestService.class);
    private final MonitorService monitorService;
    private final HealthCheckService healthCheck;
    private final Map<String, TimeFrame> timeFrames = new HashMap<>();

    @Autowired(required = true)
    public HealthRestService(MonitorService monitorService, HealthCheckService healthCheck) {
        this.monitorService = monitorService;
        this.healthCheck = healthCheck;
        timeFrames.put("1min", TimeFrame.minutes(1));
        timeFrames.put("30min", TimeFrame.minutes(30));
        timeFrames.put("24h", TimeFrame.hours(24));
    }
    @GET
    @Path("/slowHttpRequests/{timeFrame}")
    @Produces("application/json")
    public Response slowHttpRequests(@PathParam("timeFrame") String timeFrameName ) {
        TimeFrame timeFrame = timeFrames.get(timeFrameName);
        List<HttpRequestMeasurement> slowHttpRequests = monitorService.getSlowHttpRequests(timeFrame);
        return Response.ok().entity(slowHttpRequests).build();
    }

    @GET
    @Path("/ping")
    @Produces("application/json")
    public Response ping() {
        HealthCheckResult healthCheckResult = healthCheck.healthCheck();
        if ( healthCheckResult.isHealthy() ) {
            return Response.ok().entity(healthCheckResult).build();
        } else {
            logger.warn("service unhealthy.", healthCheckResult);
            return Response.serverError().entity(healthCheckResult).build();
        }
    }
}
