package com.wikia.search.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class CircularBuffer {
    private final char buffer[];
    private int pos = 0;
    private int size = 0;

    public CircularBuffer(int maxSize) {
        buffer = new char[maxSize];
    }

    public int getMaxSize() {
        return buffer.length;
    }

    public int getSize() {
        return size;
    }

    public void push( char element ) {
        if ( size >= buffer.length ) {
            throw new IllegalStateException("Trying to push but CircularBuffer is full.");
        }
        buffer[ startWritingAt() ] = element;
        size++;
    }

    public char peek() {
        if ( size == 0 ) {
            throw new IllegalStateException("Trying to peek but CircularBuffer is empty.");
        }
        return buffer[pos];
    }

    public void pop() {
        if ( size == 0 ) {
            throw new IllegalStateException("Trying to pop but CircularBuffer is empty");
        }
        pos ++;
        if ( pos >= buffer.length ) pos -= buffer.length;
        size--;
    }

    public boolean isFull() {
        return getSize() == buffer.length;
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public int readSomeFrom( Reader reader ) throws IOException {
        int endPos = startWritingAt();
        int spaceAtTheEndOfBuffer = buffer.length - endPos;
        if( spaceAtTheEndOfBuffer >= buffer.length - size ) {
            spaceAtTheEndOfBuffer = buffer.length - size;
        }
        int read = reader.read( buffer, endPos, spaceAtTheEndOfBuffer );
        if ( read != -1 ) {
            size += read;
        }
        return read;
    }

    /**
     * Takes form reader as much as you can. Returns true if end of input. Returns false when buffer is full.
     * @param reader input buffer to read from
     * @return true if end of file. This means subsequent call will not read anything. This call might have read something into buffer.
     * @throws IOException
     */
    public boolean fillFrom( Reader reader ) throws IOException {
        boolean readAnything = false;
        while ( !isFull() ) {
            if ( readSomeFrom( reader ) == -1 ) {
                return !readAnything;
            }
            readAnything = true;
        }
        return false;
    }

    /**
     * Write content to output buffer
     * @param writer output buffer to write to.
     * @throws IOException
     */
    public void writeTo( Writer writer ) throws IOException {
        int elementsAtTheEnd = Math.min( size, buffer.length - pos );

        writer.write( buffer, pos, elementsAtTheEnd );
        if ( elementsAtTheEnd != size ) {
            writer.write( buffer, 0, size - elementsAtTheEnd );
        }
    }

    /**
     * Write content to output buffer
     * @param outBuffer output buffer to write to
     */
    public void writeTo( char outBuffer[] ) {
        int elementsAtTheEnd = Math.min( size, buffer.length - pos );

        System.arraycopy( buffer, pos, outBuffer, 0, elementsAtTheEnd );
        if ( elementsAtTheEnd != size ) {
            System.arraycopy( buffer, 0, outBuffer, elementsAtTheEnd, size - elementsAtTheEnd );
        }
    }

    public String contentAsString() {
        StringWriter stringWriter = new StringWriter( size );
        try {
            writeTo(stringWriter);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException( "String writter thrown IOException.", e );
        }
        return stringWriter.toString();
    }

    private int startWritingAt() {
        int startWritingAt = pos + size;
        if ( startWritingAt >= buffer.length ) {
            startWritingAt -= buffer.length;
        }
        return startWritingAt;
    }
}
