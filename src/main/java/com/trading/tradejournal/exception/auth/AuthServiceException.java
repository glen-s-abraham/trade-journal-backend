package com.trading.tradejournal.exception.auth;

public class AuthServiceException extends RuntimeException {
    public AuthServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthServiceException(String message) {
        super(message);
    }
}
