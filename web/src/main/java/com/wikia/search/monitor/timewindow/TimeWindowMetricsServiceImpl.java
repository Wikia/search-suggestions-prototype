package com.wikia.search.monitor.timewindow;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.google.common.collect.Lists;
import com.wikia.search.monitor.TimeFrame;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeWindowMetricsServiceImpl implements TimeWindowMetricsService {
    private static Logger logger = LoggerFactory.getLogger(TimeWindowMetricsServiceImpl.class);
    private final Map<String, TimeWindowSeries<Double>> timeWindowSeriesMap;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final TimeFrame timeFrame;
    private final MetricRegistry metricRegistry;
    private final List<Double> percentiles = Lists.<Double>newArrayList(10d, 50d, 90d, 99d, 99.9d);

    public TimeWindowMetricsServiceImpl(TimeFrame timeFrame, MetricRegistry metricRegistry) {
        this.timeFrame = timeFrame;
        this.metricRegistry = metricRegistry;
        timeWindowSeriesMap = Collections.synchronizedMap(new HashMap());
        executorService.scheduleAtFixedRate(new MetricGatherAction()
                , 1
                , timeFrame.getTickDuration().getMillis()
                , TimeUnit.MILLISECONDS);
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    protected synchronized void gather() {
        for( Map.Entry<String, Histogram> entry: metricRegistry.getHistograms().entrySet() ) {
            DateTime now = DateTime.now(DateTimeZone.UTC);
            String name = entry.getKey();
            Snapshot snapshot = entry.getValue().getSnapshot();
            for ( double percentile: percentiles ) {
                gatherOne(name
                        , String.format("percentile(%.1f)", percentile)
                        , snapshot.getValue(percentile/100d),now);
            }
        }

        for( Map.Entry<String, Timer> entry: metricRegistry.getTimers().entrySet() ) {
            DateTime now = DateTime.now(DateTimeZone.UTC);
            String name = entry.getKey();
            Snapshot snapshot = entry.getValue().getSnapshot();
            for ( double percentile: percentiles ) {
                gatherOne(name
                        , String.format("percentile(%.1f)", percentile)
                        , snapshot.getValue(percentile/100d) / 1000000.0d
                        , now);
            }
            gatherOne(name, "15minute-rate", entry.getValue().getFifteenMinuteRate(), now);
            gatherOne(name, "5minute-rate", entry.getValue().getFiveMinuteRate(), now);
            gatherOne(name, "1minute-rate", entry.getValue().getOneMinuteRate(), now);
        }
    }

    protected synchronized void gatherOne( String name, String statName, double value, DateTime now ) {
        String key = name + "." + statName;
        TimeWindowSeries<Double> series;
        if ( !timeWindowSeriesMap.containsKey(key) ) {
            series = new TimeWindowSeries<>(timeFrame.getTickCount(), timeFrame.getTickDuration());
            timeWindowSeriesMap.put(key, series);
        } else {
            series = timeWindowSeriesMap.get(key);
        }
        series.update(value, now);
    }

    @Override
    public Map<String, TimeWindowSeries<Double>> getTimeWindowSeriesMap() {
        return timeWindowSeriesMap;
    }

    private class MetricGatherAction implements Runnable {
        @Override
        public void run() {
            gather();
        }
    }
}
