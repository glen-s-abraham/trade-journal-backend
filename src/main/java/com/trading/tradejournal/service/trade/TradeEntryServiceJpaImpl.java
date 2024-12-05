package com.trading.tradejournal.service.trade;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.trading.tradejournal.dto.trade.TradeEntryDto;
import com.trading.tradejournal.dto.trade.TradeEntryModificationDto;
import com.trading.tradejournal.exception.trade.TradeEntryNotFoundException;
import com.trading.tradejournal.exception.trade.TradeEntryServiceException;
import com.trading.tradejournal.model.TradeEntry;
import com.trading.tradejournal.repository.TradeEntryRepository;
import com.trading.tradejournal.service.stock.StockServiceYfinanceImpl;

import jakarta.transaction.Transactional;

@Service
public class TradeEntryServiceJpaImpl implements TradeEntryService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceYfinanceImpl.class);

    private final TradeEntryRepository tradeEntryRepository;

    public TradeEntryServiceJpaImpl(TradeEntryRepository tradeEntryRepository) {
        this.tradeEntryRepository = tradeEntryRepository;
    }

    @Override
    @Transactional
    public TradeEntryDto createTradeEntry(TradeEntryModificationDto data) {
        try {
            TradeEntry tradeEntry = TradeEntryMapper.toEntity(data);
            tradeEntry = tradeEntryRepository.save(tradeEntry);
            return TradeEntryMapper.toDto(tradeEntry);
        } catch (Exception e) {
            logger.error("Error creating trade entry", e);
            throw new TradeEntryServiceException("Error creating trade entry", e);
        }
    }

    @Override
    public List<TradeEntryDto> fetchTradeEntries() {
        try {
            List<TradeEntry> tradeEntries = tradeEntryRepository.findAll();
            List<TradeEntryDto> tradeEntryDtos = tradeEntries.stream().map(TradeEntryMapper::toDto)
                    .collect(Collectors.toList());
            return tradeEntryDtos;
        } catch (Exception e) {
            logger.error("Error creating trade entry", e);
            throw new TradeEntryServiceException("Error creating trade entry", e);
        }
    }

    @Override
    @Transactional
    public TradeEntryDto fetchTradeEntryById(Long id) {
        try {
            TradeEntry tradeEntry = tradeEntryRepository.findById(id)
                    .orElseThrow(() -> new TradeEntryNotFoundException("Trade entry not found with ID: " + id));
            return TradeEntryMapper.toDto(tradeEntry);
        } catch (TradeEntryNotFoundException e) {
            logger.error("Trade entry not found with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching trade entry with ID: {}", id, e);
            throw new TradeEntryServiceException("Error fetching trade entry with ID: " + id, e);
        }
    }

    @Override
    @Transactional
    public TradeEntryDto updateTradeEntry(Long id, TradeEntryModificationDto data) {
        try {
            TradeEntry existingTradeEntry = tradeEntryRepository.findById(id)
                    .orElseThrow(() -> new TradeEntryNotFoundException("Trade entry not found with ID: " + id));

            // Update fields in the existing trade entry
            existingTradeEntry.setStockSymbol(data.stockSymbol());
            existingTradeEntry.setTradeType(data.tradeType());
            existingTradeEntry.setQuantity(data.quantity());
            existingTradeEntry.setPrice(data.price());
            existingTradeEntry.setTradeDate(data.tradeDate());

            TradeEntry updatedTradeEntry = tradeEntryRepository.save(existingTradeEntry);
            return TradeEntryMapper.toDto(updatedTradeEntry);
        } catch (TradeEntryNotFoundException e) {
            logger.error("Trade entry not found with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating trade entry with ID: {}", id, e);
            throw new TradeEntryServiceException("Error updating trade entry with ID: " + id, e);
        }
    }

    @Override
    @Transactional
    public void deleteTradeEntryById(Long id) {
        try {
            if (!tradeEntryRepository.existsById(id)) {
                throw new TradeEntryNotFoundException("Trade entry not found with ID: " + id);
            }
            tradeEntryRepository.deleteById(id);
        } catch (TradeEntryNotFoundException e) {
            logger.error("Trade entry not found with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting trade entry with ID: {}", id, e);
            throw new TradeEntryServiceException("Error deleting trade entry with ID: " + id, e);
        }
    }

}
