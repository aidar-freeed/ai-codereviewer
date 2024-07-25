package com.adins.mss.foundation.http;

/**
 * Created by angga.permadi on 8/3/2016.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
