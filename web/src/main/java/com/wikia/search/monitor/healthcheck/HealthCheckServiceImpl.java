package com.wikia.search.monitor.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class HealthCheckServiceImpl implements HealthCheckService {
    private final List<HealthCheck> healthChecks;

    @Autowired
    public HealthCheckServiceImpl(List<HealthCheck> healthChecks) {
        this.healthChecks = healthChecks;
    }

    @Override
    public HealthCheckResult healthCheck() {
        List<HealthCheck.Result> results = new ArrayList<>();
        for ( HealthCheck healthCheck: healthChecks ) {
            results.add( healthCheck.execute() );
        }
        return new HealthCheckResult(results);
    }
}
