package com.wikia.search.lucene.analysis;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.wikia.search.util.CircularBuffer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

public class SuffixTokenizer extends Tokenizer {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLenAtt = addAttribute(PositionLengthAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final int maxTokens;
    private final Set<Character> tokenBreakers;
    private CircularBuffer buffer;
    private int tokenWritten = 0;
    private int offsetPosition = 0;

    public SuffixTokenizer(AttributeFactory factory, Reader input, Set<Character> tokenBreakers, int maxTokens, int maxTokenSize) {
        super(factory, input);
        this.maxTokens = maxTokens;
        this.tokenBreakers = tokenBreakers;
        this.buffer =  new CircularBuffer( maxTokenSize );
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if( tokenWritten == maxTokens ) {
            return false;
        }
        // read to buffer and strip tokenBreakers
        do {
            while( !buffer.isEmpty() && tokenBreakers.contains(buffer.peek()) ) {
                buffer.pop();
                offsetPosition++;
            }
        } while( !buffer.isFull() && !buffer.fillFrom( input ) );
        // wasn't able to fetch anything from input. No more tokens will be returned.
        if( buffer.isEmpty() ) {
            return false;
        }
        // set outputToken
        if( termAtt.buffer().length < buffer.getSize() ) {
            termAtt.resizeBuffer( buffer.getSize() );
        }
        termAtt.setLength( buffer.getSize() );
        buffer.writeTo(termAtt.buffer());
        posIncAtt.setPositionIncrement(1);
        posLenAtt.setPositionLength(1);
        offsetAtt.setOffset( offsetPosition, offsetPosition+buffer.getSize() );

        // move to next token beginning. Token breakers will be stripped in next iteration
        while( !buffer.isEmpty() ) {
            if( tokenBreakers.contains(buffer.peek()) ) {
                break;
            }

            buffer.pop();
            offsetPosition++;
            if ( buffer.isEmpty() ) {
                buffer.fillFrom( input );
            }
        }

        tokenWritten++;
        return true;
    }
}
