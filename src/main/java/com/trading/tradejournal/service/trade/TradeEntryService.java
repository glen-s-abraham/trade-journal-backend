package com.trading.tradejournal.service.trade;

import java.util.List;

import com.trading.tradejournal.dto.trade.TradeEntryModificationDto;
import com.trading.tradejournal.dto.trade.TradeEntryDto;

public interface TradeEntryService {
    TradeEntryDto createTradeEntry(TradeEntryModificationDto data);

    List<TradeEntryDto> fetchTradeEntries();

    TradeEntryDto fetchTradeEntryById(Long id);

    TradeEntryDto updateTradeEntry(Long id, TradeEntryModificationDto data);

    void deleteTradeEntryById(Long id);

}