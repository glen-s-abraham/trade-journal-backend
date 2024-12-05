package com.trading.tradejournal.dto.trade;

import java.time.LocalDate;
import com.trading.tradejournal.model.TradeType;

public record TradeEntryDto(
        Long id,
        String userId,
        String stockSymbol,
        TradeType tradeType,
        Integer quantity,
        Double price,
        LocalDate tradeDate) {
}
