package com.wikia.search.monitor;

import com.codahale.metrics.health.HealthCheck;

import java.util.List;

public class HealthCheckResult {
    private final List<HealthCheck.Result> partialResults;

    public HealthCheckResult(List<HealthCheck.Result> results) {
        this.partialResults = results;
    }

    public List<HealthCheck.Result> getPartialResults() {
        return partialResults;
    }

    public boolean isHealthy() {
        for ( HealthCheck.Result partialResult: getPartialResults() ) {
            if ( !partialResult.isHealthy() ) {
                return false;
            }
        }
        return true;
    }
}
