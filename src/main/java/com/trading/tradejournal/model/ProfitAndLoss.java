package com.trading.tradejournal.model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "profit_loss")
public class ProfitAndLoss extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sell_trade_id", nullable = false)
    private TradeEntry sellTrade;

    @Column(name = "stock_symbol", nullable = false)
    private String stockSymbol;

    @Column(name = "sell_date", nullable = false)
    private LocalDate sellDate;

    @Column(name = "sell_price", nullable = false)
    private double sellPrice;

    @Column(name = "sell_quantity", nullable = false)
    private Integer sellQuantity;

    @Column(name = "average_purchase_price", nullable = false)
    private Double averagePurchasePrice;

    @Column(name = "profit_or_loss", nullable = false)
    private Double profitOrLoss;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public TradeEntry getSellTrade() {
        return sellTrade;
    }

    public void setSellTrade(TradeEntry sellTrade) {
        this.sellTrade = sellTrade;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public LocalDate getSellDate() {
        return sellDate;
    }

    public void setSellDate(LocalDate sellDate) {
        this.sellDate = sellDate;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Integer getSellQuantity() {
        return sellQuantity;
    }

    public void setSellQuantity(Integer sellQuantity) {
        this.sellQuantity = sellQuantity;
    }

    public Double getAveragePurchasePrice() {
        return averagePurchasePrice;
    }

    public void setAveragePurchasePrice(Double averagePurchasePrice) {
        this.averagePurchasePrice = averagePurchasePrice;
    }

    public Double getProfitOrLoss() {
        return profitOrLoss;
    }

    public void setProfitOrLoss(Double profitOrLoss) {
        this.profitOrLoss = profitOrLoss;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
