package com.wikia.search.suggest.service;

import java.io.IOException;
import java.io.Serializable;

@SuppressWarnings("unused")
public class SearchException extends IOException implements Serializable {
    public SearchException() {
    }

    public SearchException(String message) {
        super(message);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchException(Throwable cause) {
        super(cause);
    }
}
