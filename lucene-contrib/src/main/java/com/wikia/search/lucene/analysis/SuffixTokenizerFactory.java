package com.wikia.search.lucene.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

import java.io.Reader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SuffixTokenizerFactory extends TokenizerFactory {
    private final Set<Character> tokenBreakers;
    private final int maxTokens;
    private final int maxTokenSize;

    public SuffixTokenizerFactory(Map<String, String> args) {
        super(args);
        maxTokens = getInt(args, "maxTokens", 8);
        maxTokenSize = getInt(args, "maxTokenSize", 70);
        tokenBreakers = args.containsKey("tokenBreakers") ?
                buildHashSet(args.get("tokenBreakers").toCharArray()) :
                buildHashSet(new char[]{' '});
    }

    @Override
    public Tokenizer create(AttributeSource.AttributeFactory attributeFactory, Reader reader) {
        return new SuffixTokenizer( attributeFactory, reader, tokenBreakers, maxTokens, maxTokenSize);
    }

    private static Set<Character> buildHashSet(char[] chars) {
        Set<Character> characterSet = new HashSet<>( chars.length, 0.5f );
        for( char c: chars ) {
            characterSet.add(c);
        }
        return characterSet;
    }

    public Set<Character> getTokenBreakers() {
        return tokenBreakers;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public int getMaxTokenSize() {
        return maxTokenSize;
    }
}
