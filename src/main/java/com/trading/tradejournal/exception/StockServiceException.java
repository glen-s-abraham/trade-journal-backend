package com.trading.tradejournal.exception;

public class StockServiceException extends RuntimeException {
    public StockServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockServiceException(String message) {
        super(message);
    }
}
