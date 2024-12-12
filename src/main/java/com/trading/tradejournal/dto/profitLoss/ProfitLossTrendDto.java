package com.trading.tradejournal.dto.profitLoss;

import java.time.LocalDate;

public class ProfitLossTrendDto {

    private LocalDate date;
    private Double netProfit;

    public ProfitLossTrendDto(LocalDate date, Double netProfit) {
        this.date = date;
        this.netProfit = netProfit;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(Double netProfit) {
        this.netProfit = netProfit;
    }

    @Override
    public String toString() {
        return "ProfitLossTrendDto{" +
                "date=" + date +
                ", netProfit=" + netProfit +
                '}';
    }
}
