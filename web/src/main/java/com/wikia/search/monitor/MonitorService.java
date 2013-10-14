package com.wikia.search.monitor;

import com.wikia.search.monitor.slowqueries.HttpRequestMeasurement;

import java.util.List;

public interface MonitorService {
    void monitorHttpRequest(HttpRequestMeasurement requestMeasurement);
    List<HttpRequestMeasurement> getSlowHttpRequests(TimeFrame timeFrame);
}
