package com.wikia.search.suggest.service;

import com.wikia.search.suggest.model.Suggestion;

import java.util.List;

public interface SearchService {
    List<Suggestion> search( int wikiId, String query );
}
