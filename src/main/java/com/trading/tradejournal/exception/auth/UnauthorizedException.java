package com.trading.tradejournal.exception.auth;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}