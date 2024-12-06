package com.trading.tradejournal.service.profitLoss;

import com.trading.tradejournal.dto.profitLoss.ProfitLossDto;
import com.trading.tradejournal.dto.profitLoss.ProfitLossModificationDto;
import com.trading.tradejournal.dto.trade.TradeEntryDto;
import com.trading.tradejournal.dto.trade.TradeEntryModificationDto;
import com.trading.tradejournal.model.TradeEntry;

public interface ProfitAndLossService {
    ProfitLossDto createProfitLoss(ProfitLossModificationDto data,TradeEntryDto tradeEntry);
}
