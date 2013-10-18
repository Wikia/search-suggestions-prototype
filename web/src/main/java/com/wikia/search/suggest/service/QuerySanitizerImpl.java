package com.wikia.search.suggest.service;

import org.apache.solr.client.solrj.util.ClientUtils;

import java.util.regex.Pattern;

public class QuerySanitizerImpl implements QuerySanitizer {
    private Pattern skipPattern = Pattern.compile("[\\-&_]+");
    private Pattern spacePattern = Pattern.compile("[\\-_)(*&^%$#@!\\s:\"<>(){}\\[\\]?\\/\"']+");
    private boolean trim = true;

    @Override
    public String sanitize(String query) {
        query = getSkipPattern().matcher(query).replaceAll("");
        query = getSpacePattern().matcher(query).replaceAll(" ");
        if ( isTrim() ) {
            query = query.trim();
        }
        return ClientUtils.escapeQueryChars(query);
    }

    public Pattern getSkipPattern() {
        return skipPattern;
    }

    public void setSkipPattern(Pattern skipPattern) {
        this.skipPattern = skipPattern;
    }

    public Pattern getSpacePattern() {
        return spacePattern;
    }

    public void setSpacePattern(Pattern spacePattern) {
        this.spacePattern = spacePattern;
    }

    public boolean isTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }
}
