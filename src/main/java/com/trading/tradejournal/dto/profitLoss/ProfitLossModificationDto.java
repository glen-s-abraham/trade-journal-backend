package com.trading.tradejournal.dto.profitLoss;

import java.time.LocalDate;

public record ProfitLossModificationDto(String userId,
        String stockSymbol,
        LocalDate sellDate,
        Double sellPrice,
        Integer sellQuantity,
        Double averagePurchasePrice) {
}
