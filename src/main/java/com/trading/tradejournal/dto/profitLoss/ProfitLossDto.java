package com.trading.tradejournal.dto.profitLoss;

import java.time.LocalDate;

public record ProfitLossDto(Long id,
        String userId,
        String stockSymbol,
        Long sellTradeId,
        LocalDate sellDate,
        Double sellPrice,
        Integer sellQuantity,
        Double averagePurchasePrice,
        Double profitOrLoss,
        LocalDate createdAt) {

}
