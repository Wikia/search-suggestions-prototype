package com.wikia.search.monitor.slowqueries;

import org.joda.time.Period;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorServiceFactory {
    private List<Period> timeFrames;
    private int slowQueriesCount = 10;

    public MonitorService createMonitorService() {
        Map<Period, SlowQueryMonitor<HttpRequestMeasurement>> timeFrameSlowQueryMonitorMap
                = new HashMap<>();
        for( Period timeFrame: getTimeFrames() ) {
            timeFrameSlowQueryMonitorMap.put(
                    timeFrame,
                    new GreedySlowQueryMonitor<HttpRequestMeasurement>(getSlowQueriesCount(), timeFrame) );
        }
        return new MonitorServiceImpl( timeFrameSlowQueryMonitorMap );
    }

    public List<Period> getTimeFrames() {
        return timeFrames;
    }

    public void setTimeFrames(List<Period> timeFrames) {
        this.timeFrames = timeFrames;
    }

    public int getSlowQueriesCount() {
        return slowQueriesCount;
    }

    public void setSlowQueriesCount(int slowQueriesCount) {
        this.slowQueriesCount = slowQueriesCount;
    }
}
