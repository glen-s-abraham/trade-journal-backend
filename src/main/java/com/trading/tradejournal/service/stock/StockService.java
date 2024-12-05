package com.trading.tradejournal.service.stock;

import java.math.BigDecimal;

public interface StockService {
    BigDecimal fetchStockPrice(String symbol);
}
