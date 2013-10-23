package com.wikia.search.monitor.rest;

import com.wikia.search.monitor.timewindow.TimeWindowMetricsService;
import com.wikia.search.monitor.timewindow.TimeWindowSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("metrics")
public class MetricsRestService {
    private static Logger logger = LoggerFactory.getLogger(MetricsRestService.class);
    private final List<TimeWindowMetricsService> timeWindowMetricsServiceList;

    public MetricsRestService(List<TimeWindowMetricsService> timeWindowMetricsServiceList) {
        this.timeWindowMetricsServiceList = timeWindowMetricsServiceList;
    }

    @GET
    @Path("/")
    @Produces("application/json")
    public Response metrics() {
        Map<String, Map<String, Double[]>> response = new HashMap<>();
        for( TimeWindowMetricsService service: timeWindowMetricsServiceList ) {
            Map<String,TimeWindowSeries<Double>> timeWindowSeriesMap = service.getTimeWindowSeriesMap();
            Map<String, Double[]> statSeries = new HashMap<>();
            for( Map.Entry<String, TimeWindowSeries<Double>> entry: timeWindowSeriesMap.entrySet() ) {
                statSeries.put(entry.getKey(), entry.getValue().snapshot());
            }
            response.put(service.getTimeFrame().toString(), statSeries);
        }
        return Response.ok().entity(response).build();
    }
}
