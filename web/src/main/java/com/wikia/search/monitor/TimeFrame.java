package com.wikia.search.monitor;

import org.joda.time.Period;

public class TimeFrame {
    private final Period period;

    public static TimeFrame seconds( int number ) {
        return new TimeFrame(Period.seconds(number));
    }
    public static TimeFrame minutes( int number ) {
        return new TimeFrame(Period.minutes(number));
    }
    public static TimeFrame hours( int number ) {
        return new TimeFrame(Period.hours(number));
    }

    public TimeFrame(Period period) {
        this.period = period;
    }

    public Period getPeriod() {
        return period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeFrame)) return false;

        TimeFrame timeFrame = (TimeFrame) o;

        return period.equals(timeFrame.period);
    }

    @Override
    public int hashCode() {
        return period.hashCode();
    }
}
