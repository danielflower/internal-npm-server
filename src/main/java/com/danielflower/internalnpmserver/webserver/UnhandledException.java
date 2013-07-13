package com.danielflower.internalnpmserver.webserver;

public class UnhandledException extends RuntimeException {
    public UnhandledException(Throwable cause) {
        super(cause);
    }
}
