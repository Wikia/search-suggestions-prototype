package com.wikia.search.monitor.slowqueries;

public interface SlowQueryMonitor<T> {
    void addInstance(T instance, double value);
    SlowQueryMonitorSnapshot<T> snapshot();
}
