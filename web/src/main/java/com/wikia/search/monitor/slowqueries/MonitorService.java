package com.wikia.search.monitor.slowqueries;

import org.joda.time.Period;

import java.util.List;

public interface MonitorService {
    void monitorHttpRequest(HttpRequestMeasurement requestMeasurement);
    List<HttpRequestMeasurement> getSlowHttpRequests(Period timeFrame);
}
