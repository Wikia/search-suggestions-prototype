package com.wikia.search.suggest.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class SuggestionTest {

    @Test
    public void testGetRedirects() throws Exception {
        Suggestion suggestion = new Suggestion();

        // default value is empty list
        assertThat(suggestion.getRedirects()).isEqualTo(new ArrayList<String>());
    }
}
