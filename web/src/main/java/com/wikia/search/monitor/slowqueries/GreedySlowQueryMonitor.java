package com.wikia.search.monitor.slowqueries;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class GreedySlowQueryMonitor<T> implements SlowQueryMonitor<T> {
    private final SortedSet<Entry<T>> window = new TreeSet<>();
    private final int limit;
    private final Period timeFrame;

    public GreedySlowQueryMonitor(int limit, Period timeFrame) {
        this.limit = limit;
        this.timeFrame = timeFrame;
    }

    @Override
    public synchronized void addInstance(T instance, double value) {
        removeOld();
        if( window.size() >= this.limit ) {
            if( value < window.first().value) {
                return;
            } else {
                window.remove( window.first() );
            }
        }
        window.add( new Entry<>(value, instance, DateTime.now(DateTimeZone.UTC)) );
    }

    @Override
    public synchronized SlowQueryMonitorSnapshot<T> snapshot() {
        removeOld();
        List<T> slowQueries = new ArrayList<>();
        for ( Entry<T> slowQueryEntry: window ) {
            slowQueries.add(slowQueryEntry.instance);
        }
        return new SlowQueryMonitorSnapshot<>(slowQueries);
    }

    private synchronized void removeOld() {
        // we should add another set here sorted by date
        // to reduce complexity of this method to log(n)
        DateTime dateTime = DateTime.now(DateTimeZone.UTC);
        DateTime threshold = dateTime.minus(timeFrame);
        List<Entry<T>> toRemove = new ArrayList<>();
        for( Entry<T> element: window ) {
            if ( element.dateTime.isBefore(threshold) ) {
                toRemove.add(element);
            }
        }
        window.removeAll(toRemove);
    }

    static class Entry<T> implements Comparable<Entry<T>> {
        private final double value;
        private final T instance;
        private final DateTime dateTime;

        Entry(double value, T instance, DateTime dateTime) {
            this.value = value;
            this.instance = instance;
            this.dateTime = dateTime;
        }

        @Override
        public int compareTo(Entry<T> o) {
            return (int) Math.signum(value - o.value);
        }
    }
}
