package com.wikia.search.util;

import org.testng.annotations.Test;

import java.io.StringReader;

import static org.fest.assertions.Assertions.assertThat;

public class CircularBufferTest {

    @org.testng.annotations.Test
    public void testGetMaxSize() throws Exception {
        CircularBuffer circularBuffer = new CircularBuffer( 5 );

        assertThat(circularBuffer.getMaxSize()).isEqualTo( 5 );
        circularBuffer.push('x');
        assertThat(circularBuffer.getMaxSize()).isEqualTo(5);
    }

    @org.testng.annotations.Test
    public void testGetSize() throws Exception {
        CircularBuffer circularBuffer = new CircularBuffer( 5 );

        assertThat(circularBuffer.getSize()).isEqualTo( 0 );
        circularBuffer.push('x');
        assertThat(circularBuffer.getSize()).isEqualTo(1);
        circularBuffer.push('x');
        circularBuffer.push('x');
        circularBuffer.push('x');
        circularBuffer.push('x');
        assertThat(circularBuffer.getSize()).isEqualTo(5);
    }

    @org.testng.annotations.Test
    public void testPush() throws Exception {
        CircularBuffer circularBuffer = new CircularBuffer( 3 );

        assertThat(circularBuffer.getSize()).isEqualTo( 0 );
        circularBuffer.push('x');
        assertThat(circularBuffer.peek()).isEqualTo( 'x' );
        circularBuffer.pop();
        circularBuffer.push('a');
        circularBuffer.push('b');
        circularBuffer.push('c');
        boolean exceptionThrown = false;
        try {
            circularBuffer.push( 'z' );
        } catch ( IllegalStateException ex ) {
            exceptionThrown = true;
        }
        assertThat( exceptionThrown ).isTrue();
        assertThat(circularBuffer.peek()).isEqualTo( 'a' );
        circularBuffer.pop();
        assertThat(circularBuffer.peek()).isEqualTo( 'b' );
        circularBuffer.pop();
        assertThat(circularBuffer.peek()).isEqualTo( 'c' );
        circularBuffer.pop();
        assertThat(circularBuffer.getSize()).isEqualTo(0);
    }

    @org.testng.annotations.Test
    public void testPeek() throws Exception {
        CircularBuffer circularBuffer = new CircularBuffer( 3 );

        boolean exceptionThrown = false;
        try {
            circularBuffer.peek();
        } catch ( IllegalStateException ex ) {
            exceptionThrown = true;
        }
        assertThat( exceptionThrown ).isTrue();
        circularBuffer.push( 'z' );
        assertThat(circularBuffer.peek()).isEqualTo('z');
        circularBuffer.pop();
        exceptionThrown = false;
        try {
            circularBuffer.peek();
        } catch ( IllegalStateException ex ) {
            exceptionThrown = true;
        }
        assertThat( exceptionThrown ).isTrue();
    }

    @org.testng.annotations.Test
    public void testPop() throws Exception {
        CircularBuffer circularBuffer = new CircularBuffer( 3 );

        boolean exceptionThrown = false;
        try {
            circularBuffer.pop();
        } catch ( IllegalStateException ex ) {
            exceptionThrown = true;
        }
        assertThat( exceptionThrown ).isTrue();
        circularBuffer.push( 'z' );
        circularBuffer.pop();
        exceptionThrown = false;
        try {
            circularBuffer.pop();
        } catch ( IllegalStateException ex ) {
            exceptionThrown = true;
        }
        assertThat( exceptionThrown ).isTrue();
    }

    @org.testng.annotations.Test
    public void testIsFull() throws Exception {
        CircularBuffer circularBuffer = new CircularBuffer( 3 );

        assertThat(circularBuffer.isFull()).isFalse();
        circularBuffer.push('x');
        circularBuffer.pop();
        assertThat(circularBuffer.isFull()).isFalse();
        circularBuffer.push('a');
        assertThat(circularBuffer.isFull()).isFalse();
        circularBuffer.push('b');
        assertThat(circularBuffer.isFull()).isFalse();
        circularBuffer.push('c');
        assertThat(circularBuffer.isFull()).isTrue();
    }

    @org.testng.annotations.Test
    public void testReadSomeFrom() throws Exception {
        StringReader stringReader = new StringReader( "test" );
        CircularBuffer circularBuffer = new CircularBuffer( 3 );

        assertThat(circularBuffer.readSomeFrom(stringReader)).isEqualTo(3);
        assertThat(circularBuffer.contentAsString()).isEqualTo("tes");
        circularBuffer.pop();
        circularBuffer.pop();
        assertThat(circularBuffer.contentAsString()).isEqualTo("s");
        assertThat(circularBuffer.readSomeFrom(stringReader)).isEqualTo(1);
        assertThat(circularBuffer.contentAsString()).isEqualTo("st");
        assertThat(circularBuffer.readSomeFrom(stringReader)).isEqualTo(-1);

    }

    @org.testng.annotations.Test
    public void testFillFrom() throws Exception {
        StringReader stringReader = new StringReader( "test_foo" );
        CircularBuffer circularBuffer = new CircularBuffer( 3 );

        assertThat(circularBuffer.fillFrom(stringReader)).isFalse();
        assertThat(circularBuffer.contentAsString()).isEqualTo("tes");
        circularBuffer.pop();
        circularBuffer.pop();
        assertThat(circularBuffer.contentAsString()).isEqualTo("s");
        assertThat(circularBuffer.fillFrom(stringReader)).isFalse();
        assertThat(circularBuffer.contentAsString()).isEqualTo("st_");
        circularBuffer.pop();
        circularBuffer.pop();
        assertThat(circularBuffer.fillFrom(stringReader)).isFalse();
        assertThat(circularBuffer.contentAsString()).isEqualTo("_fo");
        circularBuffer.pop();
        circularBuffer.pop();
        assertThat(circularBuffer.fillFrom(stringReader)).isFalse();
        assertThat(circularBuffer.contentAsString()).isEqualTo("oo");
        assertThat(circularBuffer.fillFrom(stringReader)).isTrue();
    }

    @Test
    public void testContentAsString() throws Exception {
        CircularBuffer circularBuffer = new CircularBuffer( 3 );

        assertThat(circularBuffer.contentAsString()).isEqualTo("");
        circularBuffer.push('a');
        assertThat(circularBuffer.contentAsString()).isEqualTo("a");
        circularBuffer.push('b');
        assertThat(circularBuffer.contentAsString()).isEqualTo("ab");
        circularBuffer.push('c');
        assertThat(circularBuffer.contentAsString()).isEqualTo("abc");
        circularBuffer.pop();
        assertThat(circularBuffer.contentAsString()).isEqualTo("bc");
        circularBuffer.pop();
        circularBuffer.push('d');
        assertThat(circularBuffer.contentAsString()).isEqualTo("cd");
        circularBuffer.pop();
        circularBuffer.push('e');
        assertThat(circularBuffer.contentAsString()).isEqualTo("de");
        circularBuffer.pop();
        circularBuffer.push('f');
        assertThat(circularBuffer.contentAsString()).isEqualTo("ef");
        circularBuffer.pop();
        circularBuffer.push('g');
        assertThat(circularBuffer.contentAsString()).isEqualTo("fg");
        circularBuffer.pop();
        circularBuffer.pop();
        assertThat(circularBuffer.contentAsString()).isEqualTo("");
    }
}
