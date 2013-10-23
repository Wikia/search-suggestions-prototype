package com.wikia.search.monitor.timewindow;

import com.wikia.search.monitor.TimeFrame;

import java.util.Map;

public interface TimeWindowMetricsService {
    TimeFrame getTimeFrame();
    Map<String, TimeWindowSeries<Double>> getTimeWindowSeriesMap();
}
