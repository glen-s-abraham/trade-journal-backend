package com.trading.tradejournal.service.profitLoss;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.stereotype.Service;

import com.trading.tradejournal.dto.profitLoss.ProfitLossDto;
import com.trading.tradejournal.dto.profitLoss.ProfitLossModificationDto;
import com.trading.tradejournal.dto.trade.TradeEntryDto;
import com.trading.tradejournal.exception.profitLoss.ProfitLossServiceException;
import com.trading.tradejournal.model.ProfitAndLoss;
import com.trading.tradejournal.model.TradeEntry;
import com.trading.tradejournal.repository.ProfitAndLossRepository;

import jakarta.transaction.Transactional;

@Service
public class ProfitAndLossServiceJpaImpl implements ProfitAndLossService {

    private final ProfitAndLossRepository profitAndLossRepository;
    private final ModelMapper modelMapper;

    public ProfitAndLossServiceJpaImpl(ProfitAndLossRepository profitAndLossRepository) {
        this.profitAndLossRepository = profitAndLossRepository;
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);
    }

    @Override
    @Transactional
    public ProfitLossDto createProfitLoss(ProfitLossModificationDto data, TradeEntryDto tradeEntryDto)
            throws ProfitLossServiceException {
        try {
            TradeEntry tradeEntry = modelMapper.map(tradeEntryDto, TradeEntry.class);
            ProfitAndLoss profitAndLoss = ProfitAndLossMapper.toEntity(data, tradeEntry);
            profitAndLoss = profitAndLossRepository.save(profitAndLoss);
            return ProfitAndLossMapper.toDto(profitAndLoss);
        } catch (Exception ex) {
            throw new ProfitLossServiceException("Error creating Profit loss entry", ex);
        }
    }

}
