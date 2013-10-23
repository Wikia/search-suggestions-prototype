package com.wikia.search.monitor;

import org.joda.time.Duration;

public class TimeFrame {
    private final Duration tickDuration;
    private final int tickCount;

    public static TimeFrame secondBased( int ticks ) {
        return new TimeFrame(Duration.standardSeconds(1), ticks);
    }

    public TimeFrame(Duration tickDuration, int tickCount) {
        this.tickDuration = tickDuration;
        this.tickCount = tickCount;
    }

    public Duration getTickDuration() {
        return tickDuration;
    }

    public int getTickCount() {
        return tickCount;
    }

    public Duration getDuration() {
        return Duration.millis(tickDuration.getMillis() * tickCount);
    }

    @Override
    public String toString() {
        return getDuration().toString();
    }
}
