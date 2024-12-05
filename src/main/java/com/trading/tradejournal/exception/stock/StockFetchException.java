package com.trading.tradejournal.exception.stock;

public class StockFetchException extends RuntimeException {
    public StockFetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockFetchException(String message) {
        super(message);
    }
}