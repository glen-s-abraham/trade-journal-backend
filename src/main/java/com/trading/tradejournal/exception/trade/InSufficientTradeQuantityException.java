package com.trading.tradejournal.exception.trade;

public class InSufficientTradeQuantityException extends RuntimeException {
    public InSufficientTradeQuantityException(String message, Throwable cause) {
        super(message, cause);
    }

    public InSufficientTradeQuantityException(String message) {
        super(message);
    }
}