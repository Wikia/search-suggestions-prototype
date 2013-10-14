package com.wikia.search.monitor.slowqueries;

import org.joda.time.DateTime;

public class HttpRequestMeasurement {
    private final String uri;
    private final double milliseconds;
    private final DateTime dateTime;

    public HttpRequestMeasurement(String uri, double milliseconds, DateTime dateTime) {
        this.uri = uri;
        this.milliseconds = milliseconds;
        this.dateTime = dateTime;
    }

    public String getUri() {
        return uri;
    }

    public double getMilliseconds() {
        return milliseconds;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return uri +
                " (" + milliseconds +
                "ms) " + dateTime;
    }
}
