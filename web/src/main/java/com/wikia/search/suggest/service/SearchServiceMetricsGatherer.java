package com.wikia.search.suggest.service;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.wikia.search.suggest.model.Suggestion;

import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

public class SearchServiceMetricsGatherer implements SearchService {
    private final SearchService searchService;
    private final MetricRegistry registry;
    private final Histogram resultCounts;
    private final Timer timer;

    public SearchServiceMetricsGatherer(SearchService searchService, MetricRegistry registry) {
        this.searchService = searchService;
        this.registry = registry;
        resultCounts = this.registry.histogram(name(SearchServiceMetricsGatherer.class, "result-counts"));
        timer = registry.timer(name(SearchServiceMetricsGatherer.class, "requests-time"));
    }

    @Override
    public List<Suggestion> search(int wikiId, String query) throws SearchException {
        Timer.Context timeContext = timer.time();
        List<Suggestion> searchResult = null;
        try {
            searchResult = searchService.search(wikiId, query);
            return searchResult;
        } finally {
            // time measure
            timeContext.stop();
            // result count histogram
            if ( searchResult != null ) {
                resultCounts.update(searchResult.size());
            }
        }

    }
}
