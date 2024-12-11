package com.trading.tradejournal.service.profitLoss;

import java.util.List;

import com.trading.tradejournal.dto.profitLoss.ProfitLossDto;
import com.trading.tradejournal.dto.profitLoss.ProfitLossModificationDto;
import com.trading.tradejournal.dto.profitLoss.ProfitLossReport;
import com.trading.tradejournal.dto.profitLoss.TotalProfitAndLoss;
import com.trading.tradejournal.dto.trade.TradeEntryDto;


public interface ProfitAndLossService {
    ProfitLossDto createProfitLoss(ProfitLossModificationDto data, TradeEntryDto tradeEntry);

    List<ProfitLossReport> fetchcurrentProfitAndLoss(String userId);

    TotalProfitAndLoss fetchTotalProfitAndLoss(String userId);
}
