package com.wikia.search.monitor.slowqueries;

import java.util.List;

public class SlowQueryMonitorSnapshot<T> {
    private List<T> slowQueries;

    public SlowQueryMonitorSnapshot(List<T> slowQueries) {
        this.slowQueries = slowQueries;
    }

    public List<T> getSlowQueries() {
        return slowQueries;
    }
}
