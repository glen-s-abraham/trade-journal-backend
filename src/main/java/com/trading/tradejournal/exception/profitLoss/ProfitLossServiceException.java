package com.trading.tradejournal.exception.profitLoss;

public class ProfitLossServiceException extends RuntimeException {

    public ProfitLossServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfitLossServiceException(String message) {
        super(message);
    }
}