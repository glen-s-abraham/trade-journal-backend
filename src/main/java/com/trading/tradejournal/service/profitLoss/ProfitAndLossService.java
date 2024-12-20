package com.trading.tradejournal.service.profitLoss;

import java.time.LocalDate;
import java.util.List;

import com.trading.tradejournal.dto.profitLoss.ProfitLossDto;
import com.trading.tradejournal.dto.profitLoss.ProfitLossModificationDto;
import com.trading.tradejournal.dto.profitLoss.ProfitLossReport;
import com.trading.tradejournal.dto.profitLoss.ProfitLossTrendDto;
import com.trading.tradejournal.dto.profitLoss.TotalProfitAndLoss;
import com.trading.tradejournal.dto.trade.TradeEntryDto;
import com.trading.tradejournal.dto.trade.TradeEntryNetDto;
import com.trading.tradejournal.service.profitLoss.enums.ProfitAndLossInterval;

public interface ProfitAndLossService {
    ProfitLossDto createProfitLoss(ProfitLossModificationDto data, TradeEntryDto tradeEntry);

    List<ProfitLossReport> fetchcurrentProfitAndLoss(String userId);

    List<ProfitLossReport> fetchcurrentProfitAndLoss(String userId, LocalDate startDate, LocalDate endDate);

    TotalProfitAndLoss fetchTotalProfitAndLoss(String userId);

    TotalProfitAndLoss fetchTotalProfitAndLoss(String userId, LocalDate starDate, LocalDate endDate);

    List<ProfitLossTrendDto> getProfitLossTrend(String userId, LocalDate startDate, LocalDate endDate,
            ProfitAndLossInterval interval);
}
