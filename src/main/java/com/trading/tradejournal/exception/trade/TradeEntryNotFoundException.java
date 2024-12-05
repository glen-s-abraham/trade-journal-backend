package com.trading.tradejournal.exception.trade;

public class TradeEntryNotFoundException extends RuntimeException {
    public TradeEntryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TradeEntryNotFoundException(String message) {
        super(message);
    }
}