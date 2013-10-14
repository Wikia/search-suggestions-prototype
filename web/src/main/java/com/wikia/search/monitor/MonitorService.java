package com.wikia.search.monitor;

import java.util.List;

public interface MonitorService {
    void monitorHttpRequest(HttpRequestMeasurement requestMeasurement);
    List<HttpRequestMeasurement> getSlowHttpRequests(TimeFrame timeFrame);
}
