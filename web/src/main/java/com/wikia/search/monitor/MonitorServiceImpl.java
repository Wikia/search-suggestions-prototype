package com.wikia.search.monitor;

import com.wikia.search.monitor.slowqueries.HttpRequestMeasurement;
import com.wikia.search.monitor.slowqueries.SlowQueryMonitor;

import java.util.List;
import java.util.Map;

public class MonitorServiceImpl implements MonitorService {
    private final Map<TimeFrame, SlowQueryMonitor<HttpRequestMeasurement>> slowQueryMonitorMap;
    public MonitorServiceImpl(Map<TimeFrame, SlowQueryMonitor<HttpRequestMeasurement>> slowQueryMonitorMap ) {
        this.slowQueryMonitorMap = slowQueryMonitorMap;
    }

    @Override
    public void monitorHttpRequest(HttpRequestMeasurement requestMeasurement) {
        for( SlowQueryMonitor<HttpRequestMeasurement> monitor: slowQueryMonitorMap.values() ) {
            monitor.addInstance(requestMeasurement, requestMeasurement.getMilliseconds());
        }
    }

    @Override
    public List<HttpRequestMeasurement> getSlowHttpRequests(TimeFrame timeFrame) {
        if ( !slowQueryMonitorMap.containsKey(timeFrame) ) {
            throw new IllegalArgumentException("No data is gathered for this time frame " + timeFrame);
        }
        SlowQueryMonitor<HttpRequestMeasurement> httpRequestMeasurementSlowQueryMonitor
                = slowQueryMonitorMap.get(timeFrame);
        return httpRequestMeasurementSlowQueryMonitor.snapshot().getSlowQueries();
    }

}
