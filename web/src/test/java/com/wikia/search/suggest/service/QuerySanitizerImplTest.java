package com.wikia.search.suggest.service;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.fest.assertions.Assertions.assertThat;

public class QuerySanitizerImplTest {

    @Test
    public void testSanitize() throws Exception {
        QuerySanitizerImpl querySanitizer = new QuerySanitizerImpl();
        assertThat(querySanitizer.sanitize(" ")).isEqualTo("");
        assertThat(querySanitizer.sanitize("a")).isEqualTo("a");
        assertThat(querySanitizer.sanitize("a !@#$:\" a")).isEqualTo("a\\ a");
        assertThat(querySanitizer.sanitize(" a ")).isEqualTo("a");
        assertThat(querySanitizer.sanitize("")).isEqualTo("");
    }

    @Test
    public void testSanitizeNonTrim() throws Exception {
        QuerySanitizerImpl querySanitizer = new QuerySanitizerImpl();
        querySanitizer.setTrim(false);
        assertThat(querySanitizer.sanitize(" ")).isEqualTo("\\ ");
        assertThat(querySanitizer.sanitize("a")).isEqualTo("a");
        assertThat(querySanitizer.sanitize("a !@#$:\" a")).isEqualTo("a\\ a");
        assertThat(querySanitizer.sanitize(" a ")).isEqualTo("\\ a\\ ");
        assertThat(querySanitizer.sanitize("")).isEqualTo("");
    }

    @Test
    public void testSanitizeSkipPattern() throws Exception {
        QuerySanitizerImpl querySanitizer = new QuerySanitizerImpl();
        querySanitizer.setSkipPattern(Pattern.compile("[!]+"));
        assertThat(querySanitizer.sanitize(" ")).isEqualTo("");
        assertThat(querySanitizer.sanitize("a!")).isEqualTo("a");
        assertThat(querySanitizer.sanitize("a !@#$:\" a")).isEqualTo("a\\ a");
        assertThat(querySanitizer.sanitize(" a ")).isEqualTo("a");
        assertThat(querySanitizer.sanitize("")).isEqualTo("");
    }
    @Test
    public void testSanitizeSpacePattern() throws Exception {
        QuerySanitizerImpl querySanitizer = new QuerySanitizerImpl();
        querySanitizer.setSpacePattern(Pattern.compile("[! ]+"));
        assertThat(querySanitizer.sanitize(" ! ")).isEqualTo("");
        assertThat(querySanitizer.sanitize("a!")).isEqualTo("a");
        assertThat(querySanitizer.sanitize("a !@#$:\" a")).isEqualTo("a\\ @#$\\:\\\"\\ a");
        assertThat(querySanitizer.sanitize(" a ")).isEqualTo("a");
        assertThat(querySanitizer.sanitize("")).isEqualTo("");
    }

    @Test
    public void testGetSkipPattern() throws Exception {
        QuerySanitizerImpl querySanitizer = new QuerySanitizerImpl();
        // has default value
        assertThat(querySanitizer.getSkipPattern()).isNotNull();
    }

    @Test
    public void testSetSkipPattern() throws Exception {
        QuerySanitizerImpl querySanitizer = new QuerySanitizerImpl();
        Pattern pattern = Pattern.compile("");

        querySanitizer.setSkipPattern(pattern);

        assertThat(querySanitizer.getSkipPattern()).isEqualTo(pattern);
    }

    @Test
    public void testGetSpacePattern() throws Exception {
        QuerySanitizerImpl querySanitizer = new QuerySanitizerImpl();
        // has default value
        assertThat(querySanitizer.getSpacePattern()).isNotNull();
    }

    @Test
    public void testSetSpacePattern() throws Exception {
        QuerySanitizerImpl querySanitizer = new QuerySanitizerImpl();
        Pattern pattern = Pattern.compile("");

        querySanitizer.setSpacePattern(pattern);

        assertThat(querySanitizer.getSpacePattern()).isEqualTo(pattern);
    }

    @Test
    public void testIsTrim() throws Exception {
        QuerySanitizerImpl querySanitizer = new QuerySanitizerImpl();

        querySanitizer.setTrim(false);

        assertThat(querySanitizer.isTrim()).isFalse();
    }

    @Test
    public void testSetTrim() throws Exception {
        QuerySanitizerImpl querySanitizer = new QuerySanitizerImpl();
        // true by default
        assertThat(querySanitizer.isTrim()).isTrue();
    }
}
