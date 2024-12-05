package com.trading.tradejournal.service;

import java.math.BigDecimal;

public interface StockService {
    BigDecimal fetchStockPrice(String symbol);
}
