package com.trading.tradejournal.dto.trade;

import java.time.LocalDate;

import com.trading.tradejournal.model.TradeType;

public record TradeEntryModificationDto(String userId,
        String stockSymbol,
        TradeType tradeType,
        Integer quantity,
        Double price,
        LocalDate tradeDate) {
                public TradeEntryModificationDto withUserId(String userId) {
                        return new TradeEntryModificationDto(userId, stockSymbol, tradeType, quantity, price, tradeDate);
                }
}
