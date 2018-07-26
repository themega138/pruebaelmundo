package com.em.test.callcenter.core;

public class DispatcherException extends RuntimeException {

    public DispatcherException() {
    }

    public DispatcherException(String message) {
        super(message);
    }

    public DispatcherException(String message, Throwable cause) {
        super(message, cause);
    }
}
