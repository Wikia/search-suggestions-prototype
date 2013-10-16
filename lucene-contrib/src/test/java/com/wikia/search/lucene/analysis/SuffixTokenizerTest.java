package com.wikia.search.lucene.analysis;

import junit.framework.Assert;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeSource;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

public class SuffixTokenizerTest {

    @Test
    public void testIncrementToken() throws Exception {
        SuffixTokenizer suffixTokenizer = buildSuffixTokenizer("a b c", 2, 4);

        assertThat(suffixTokenizer.incrementToken()).isTrue();
        assertThat(suffixTokenizer.incrementToken()).isTrue();
        assertThat(suffixTokenizer.incrementToken()).isFalse();
    }

    @Test
    public void testTokens() throws Exception {
        assertThat( tokenize( "asd bsd csd", 3, 11) ).isEqualTo(Lists.newArrayList("asd bsd csd", "bsd csd", "csd"));
        assertThat( tokenize( "    asd bsd csd", 3, 11) ).isEqualTo(Lists.newArrayList("asd bsd csd", "bsd csd", "csd"));
        assertThat( tokenize( "asd bsd csd", 3, 10) ).isEqualTo(Lists.newArrayList("asd bsd cs", "bsd csd", "csd"));
        assertThat( tokenize( "asd bsd csd", 3, 6) ).isEqualTo( Lists.newArrayList("asd bs", "bsd cs", "csd"));
        assertThat( tokenize( "asd bsd csd", 2, 11) ).isEqualTo(Lists.newArrayList("asd bsd csd", "bsd csd"));
        assertThat( tokenize( "asd bsd csd", 3, 1) ).isEqualTo(Lists.newArrayList("a", "b", "c"));
        assertThat( tokenize( "asd   bsd  csd", 3, 14) ).isEqualTo(Lists.newArrayList("asd   bsd  csd", "bsd  csd", "csd"));
        assertThat( tokenize( "asd   bsd  csd", 3, 13) ).isEqualTo(Lists.newArrayList("asd   bsd  cs", "bsd  csd", "csd"));
        assertThat( tokenize( "", 3, 13) ).isEqualTo(Lists.newArrayList());
        assertThat( tokenize( "xzc   zxcasd   s", 1, 1) ).isEqualTo(Lists.newArrayList("x"));
        assertThat( tokenize( "     ", 3, 11) ).isEqualTo(Lists.newArrayList());
        assertThat( tokenize( "12345678901234567890", 3, 20) ).isEqualTo(Lists.newArrayList("12345678901234567890"));
    }

    private List<String> tokenize( String str , int maxTokens, int maxTokenSize ) throws IOException {
        SuffixTokenizer suffixTokenizer = buildSuffixTokenizer(str, maxTokens, maxTokenSize);

        List<String> strings = new ArrayList<>();
        while( suffixTokenizer.incrementToken() ) {
            strings.add( suffixTokenizer.getAttribute(CharTermAttribute.class).toString() );
        }
        return strings;
    }

    private SuffixTokenizer buildSuffixTokenizer(String str, int maxTokens, int maxTokenSize) {
        Token.TokenAttributeFactory tokenAttributeFactory = new Token.TokenAttributeFactory( new AttributeSource.AttributeFactory() {
            @Override
            public AttributeImpl createAttributeInstance(Class<? extends Attribute> aClass) {
                if( aClass.equals(CharTermAttribute.class) ) {
                    return new CharTermAttributeImpl();
                } if( aClass.equals(KeywordAttribute.class) ) {
                    return new KeywordAttributeImpl();
                } else if ( aClass.equals(PositionIncrementAttribute.class) ) {
                    return new PositionIncrementAttributeImpl();
                } else {
                    Assert.fail("Unexpected attribute instance" + aClass.getCanonicalName());
                    throw new IllegalStateException();
                }
            }
        });
        Set<Character> tokenBreakers = new HashSet<>();
        tokenBreakers.add(' ');
        return new SuffixTokenizer(
                tokenAttributeFactory,
                new StringReader(str),
                tokenBreakers,
                maxTokens,
                maxTokenSize);
    }
}
