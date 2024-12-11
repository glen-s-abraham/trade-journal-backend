package com.trading.tradejournal.service.trade;

import java.time.LocalDate;
import java.util.List;

import com.trading.tradejournal.dto.trade.TradeEntryModificationDto;
import com.trading.tradejournal.dto.trade.TradeEntryNetDto;
import com.trading.tradejournal.dto.trade.TradeEntryDto;

public interface TradeEntryService {
    TradeEntryDto createTradeEntry(TradeEntryModificationDto data);

    List<TradeEntryDto> fetchTradeEntries();

    List<TradeEntryDto> fetchTradeEntriesByUserId(String userId);

    List<TradeEntryDto> fetchTradeEntriesByUserId(String userId, LocalDate starDate, LocalDate endDate);

    TradeEntryDto fetchTradeEntryById(Long id);

    TradeEntryDto fetchTradeEntryByIdAndUserId(Long id, String userId);

    List<TradeEntryNetDto> fetchNetPositions(String userId);

    TradeEntryDto updateTradeEntry(Long id, TradeEntryModificationDto data);

    TradeEntryDto updateTradeEntryForUser(Long id, String userId, TradeEntryModificationDto data);

    void deleteTradeEntryById(Long id);

    void deleteTradeEntryByIdForUser(Long id, String UserId);

}