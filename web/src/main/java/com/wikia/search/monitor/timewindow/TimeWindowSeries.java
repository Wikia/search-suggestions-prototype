package com.wikia.search.monitor.timewindow;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

public class TimeWindowSeries<T> {
    private final CircularFifoBuffer buffer;
    private final Duration timeTick;
    private DateTime current;

    public TimeWindowSeries(int tickCapacity, Duration timeTick) {
        this.timeTick = timeTick;
        buffer = new CircularFifoBuffer(tickCapacity);
        current = DateTime.now(DateTimeZone.UTC);
    }

    public synchronized void update(Double element, DateTime at) {
        while( current.plus(timeTick).isBefore(at) ) {
            current = current.plus(timeTick);
            buffer.add(element);
        }
    }

    public synchronized T[] snapshot() {
        return (T[]) buffer.toArray();
    }
}
