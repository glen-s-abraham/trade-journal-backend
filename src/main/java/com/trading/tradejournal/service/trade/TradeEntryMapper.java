package com.trading.tradejournal.service.trade;

import com.trading.tradejournal.dto.trade.TradeEntryDto;
import com.trading.tradejournal.dto.trade.TradeEntryModificationDto;
import com.trading.tradejournal.model.TradeEntry;

public class TradeEntryMapper {
    public static TradeEntry toEntity(TradeEntryModificationDto dto) {
        TradeEntry tradeEntry = new TradeEntry();
        tradeEntry.setUserId(dto.userId());
        tradeEntry.setStockSymbol(dto.stockSymbol());
        tradeEntry.setTradeType(dto.tradeType());
        tradeEntry.setQuantity(dto.quantity());
        tradeEntry.setPrice(dto.price());
        tradeEntry.setTradeDate(dto.tradeDate());
        return tradeEntry;
    }

    public static TradeEntryDto toDto(TradeEntry entity) {
        return new TradeEntryDto(
                entity.getId(),
                entity.getUserId(),
                entity.getStockSymbol(),
                entity.getTradeType(),
                entity.getQuantity(),
                entity.getPrice(),
                entity.getTradeDate());
    }
}
