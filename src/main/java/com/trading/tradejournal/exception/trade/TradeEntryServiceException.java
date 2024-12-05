package com.trading.tradejournal.exception.trade;

public class TradeEntryServiceException extends RuntimeException {
    public TradeEntryServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TradeEntryServiceException(String message) {
        super(message);
    }
}