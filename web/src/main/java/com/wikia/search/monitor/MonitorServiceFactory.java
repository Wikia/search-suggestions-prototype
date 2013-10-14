package com.wikia.search.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorServiceFactory {
    private List<TimeFrame> timeFrames;
    private int slowQueriesCount = 10;

    public MonitorService createMonitorService() {
        Map<TimeFrame, SlowQueryMonitor<HttpRequestMeasurement>> timeFrameSlowQueryMonitorMap
                = new HashMap<>();
        for( TimeFrame timeFrame: getTimeFrames() ) {
            timeFrameSlowQueryMonitorMap.put(
                    timeFrame,
                    new GreedySlowQueryMonitor<HttpRequestMeasurement>(getSlowQueriesCount(), timeFrame) );
        }
        return new MonitorServiceImpl( timeFrameSlowQueryMonitorMap );
    }

    public List<TimeFrame> getTimeFrames() {
        return timeFrames;
    }

    public void setTimeFrames(List<TimeFrame> timeFrames) {
        this.timeFrames = timeFrames;
    }

    public int getSlowQueriesCount() {
        return slowQueriesCount;
    }

    public void setSlowQueriesCount(int slowQueriesCount) {
        this.slowQueriesCount = slowQueriesCount;
    }
}
