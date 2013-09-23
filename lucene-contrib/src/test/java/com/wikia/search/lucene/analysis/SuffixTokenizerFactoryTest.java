package com.wikia.search.lucene.analysis;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

public class SuffixTokenizerFactoryTest {

    /*
    @Test
    public void testCreate() throws Exception {

    }
    */

    @Test
    public void testGetTokenBreakers() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("tokenBreakers", "@ ");

        assertThat(new SuffixTokenizerFactory(map).getTokenBreakers()).contains('@', ' ');
    }

    @Test
    public void testGetMaxTokens() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("maxTokens", "23");

        assertThat(new SuffixTokenizerFactory(map).getMaxTokens()).isEqualTo(23);
    }

    @Test
    public void testGetMaxTokenSize() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("maxTokenSize", "123");

        assertThat(new SuffixTokenizerFactory(map).getMaxTokenSize()).isEqualTo(123);
    }
}
