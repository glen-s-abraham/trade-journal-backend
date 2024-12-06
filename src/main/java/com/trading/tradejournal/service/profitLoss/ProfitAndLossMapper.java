package com.trading.tradejournal.service.profitLoss;

import com.trading.tradejournal.dto.profitLoss.ProfitLossDto;
import com.trading.tradejournal.dto.profitLoss.ProfitLossModificationDto;
import com.trading.tradejournal.model.ProfitAndLoss;
import com.trading.tradejournal.model.TradeEntry;

public class ProfitAndLossMapper {
    private static Double calculatePnL(Double avgPrice, Double sellPrice, Integer qty) {
        return (sellPrice - avgPrice) * qty;
    }

    public static ProfitAndLoss toEntity(ProfitLossModificationDto dto, TradeEntry tradeEntry) {
        ProfitAndLoss profitLoss = new ProfitAndLoss();
        profitLoss.setUserId(dto.userId());
        profitLoss.setStockSymbol(dto.stockSymbol());
        profitLoss.setSellTrade(tradeEntry);
        profitLoss.setSellDate(dto.sellDate());
        profitLoss.setSellPrice(dto.sellPrice());
        profitLoss.setSellQuantity(dto.sellQuantity());
        profitLoss.setAveragePurchasePrice(dto.averagePurchasePrice());
        profitLoss.setProfitOrLoss(calculatePnL(dto.averagePurchasePrice(), dto.sellPrice(), dto.sellQuantity()));
        return profitLoss;
    }

    public static ProfitLossDto toDto(ProfitAndLoss entity) {
        return new ProfitLossDto(
                entity.getId(),
                entity.getUserId(),
                entity.getStockSymbol(),

                // Retrieve the ID from the sellTrade field
                entity.getSellTrade() != null ? entity.getSellTrade().getId() : null,

                entity.getSellDate(),
                entity.getSellPrice(),
                entity.getSellQuantity(),
                entity.getAveragePurchasePrice(),
                entity.getProfitOrLoss(),
                entity.getCreatedAt());
    }
}
