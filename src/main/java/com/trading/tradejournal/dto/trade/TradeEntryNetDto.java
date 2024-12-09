package com.trading.tradejournal.dto.trade;

public class TradeEntryNetDto {
    private String stockSymbol;
    private Double averagePrice;
    private Long netQuantity;

    // Constructor
    public TradeEntryNetDto(String stockSymbol, Double averagePrice, Long netQuantity) {
        this.stockSymbol = stockSymbol;
        this.averagePrice = averagePrice;
        this.netQuantity = netQuantity;
    }

    // Getters and setters
    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Long getNetQuantity() {
        return netQuantity;
    }

    public void setNetQuantity(Long netQuantity) {
        this.netQuantity = netQuantity;
    }
}
