package com.trading.tradejournal.dto.profitLoss;

public record ProfitLossReport(String stockSymbol, Double averagePrice, Long netQuantity, Double currentMarketPrice) {
}