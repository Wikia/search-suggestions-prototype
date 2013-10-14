package com.wikia.search.monitor;

public interface SlowQueryMonitor<T> {
    void addInstance(T instance, double value);
    SlowQueryMonitorSnapshot<T> snapshot();
}
