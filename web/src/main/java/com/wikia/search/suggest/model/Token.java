package com.wikia.search.suggest.model;

public class Token {
    private String text;

    public Token(String text) {
        if( text == null ) {
            throw new IllegalArgumentException("Text cannot be null.");
        }
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
