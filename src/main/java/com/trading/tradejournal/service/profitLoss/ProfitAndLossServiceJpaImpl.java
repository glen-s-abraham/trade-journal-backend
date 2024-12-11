package com.trading.tradejournal.service.profitLoss;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.noneDSA;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.trading.tradejournal.dto.profitLoss.ProfitLossDto;
import com.trading.tradejournal.dto.profitLoss.ProfitLossModificationDto;
import com.trading.tradejournal.dto.profitLoss.ProfitLossReport;
import com.trading.tradejournal.dto.profitLoss.TotalProfitAndLoss;
import com.trading.tradejournal.dto.stock.StockPrice;
import com.trading.tradejournal.dto.trade.TradeEntryDto;
import com.trading.tradejournal.dto.trade.TradeEntryNetDto;
import com.trading.tradejournal.exception.profitLoss.ProfitLossServiceException;
import com.trading.tradejournal.model.ProfitAndLoss;
import com.trading.tradejournal.model.TradeEntry;
import com.trading.tradejournal.repository.ProfitAndLossRepository;
import com.trading.tradejournal.repository.TradeEntryRepository;
import com.trading.tradejournal.service.stock.StockService;

import jakarta.transaction.Transactional;

@Service
public class ProfitAndLossServiceJpaImpl implements ProfitAndLossService {

    private static final Logger logger = LoggerFactory.getLogger(ProfitAndLossServiceJpaImpl.class);

    private final ProfitAndLossRepository profitAndLossRepository;
    private final TradeEntryRepository tradeEntryRepository;
    private final StockService stockService;
    private final ModelMapper modelMapper;

    public ProfitAndLossServiceJpaImpl(ProfitAndLossRepository profitAndLossRepository,
            TradeEntryRepository tradeEntryRepository, @Qualifier(value = "AlphaVantage") StockService stockService) {
        this.profitAndLossRepository = profitAndLossRepository;
        this.tradeEntryRepository = tradeEntryRepository;
        this.stockService = stockService;
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

    private List<ProfitLossReport> transformNetPositions(List<TradeEntryNetDto> netPositions) {
        return netPositions.stream().map(p -> {
            Double currentPrice;
            try {
                currentPrice = stockService.fetchStockPrice(p.getStockSymbol()).doubleValue();
            } catch (Exception e) {
                currentPrice = 0d;
                logger.error("Failed to fetch stock price for symbol: {}", p.getStockSymbol(), e);
            }
            return new ProfitLossReport(p.getStockSymbol(), p.getAveragePrice(), p.getNetQuantity(),
                    currentPrice);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProfitLossReport> fetchcurrentProfitAndLoss(String userId) {
        try {
            List<TradeEntryNetDto> netPositions = tradeEntryRepository.calculateNetQuantityAndAveragePrice(userId);
            List<ProfitLossReport> profitLossStatus = transformNetPositions(netPositions);
            return profitLossStatus;
        } catch (Exception e) {
            throw new ProfitLossServiceException("Error fetching current profit and loss");
        }
    }

    @Override
    public List<ProfitLossReport> fetchcurrentProfitAndLoss(String userId, LocalDate startDate, LocalDate endDate) {
        try {
            List<TradeEntryNetDto> netPositions = tradeEntryRepository.calculateNetQuantityAndAveragePrice(userId,
                    startDate, endDate);
            List<ProfitLossReport> profitLossStatus = transformNetPositions(netPositions);
            return profitLossStatus;
        } catch (Exception e) {
            throw new ProfitLossServiceException("Error fetching current profit and loss");
        }
    }

    private TotalProfitAndLoss transformProfitAndLoss(List<ProfitLossReport> profitLossReport) {
        // Calculate total invested
        Double totalInvested = profitLossReport.stream()
                .mapToDouble(pnl -> pnl.averagePrice() * pnl.netQuantity())
                .sum();

        // Calculate total current market value
        Double totalMarketValue = profitLossReport.stream()
                .mapToDouble(pnl -> pnl.currentMarketPrice() * pnl.netQuantity())
                .sum();

        // Return TotalProfitAndLoss object
        return new TotalProfitAndLoss(totalInvested, totalMarketValue, totalMarketValue - totalInvested);
    }

    @Override
    public TotalProfitAndLoss fetchTotalProfitAndLoss(String userId) {
        try {
            // Fetch current profit and loss reports
            List<ProfitLossReport> currentProfitAndLoss = fetchcurrentProfitAndLoss(userId);
            return transformProfitAndLoss(currentProfitAndLoss);
        } catch (Exception e) {
            logger.error("Error fetching total profit and loss for user: {}", userId, e);
            throw new ProfitLossServiceException("Error fetching total profit and loss", e);
        }
    }

    @Override
    public TotalProfitAndLoss fetchTotalProfitAndLoss(String userId, LocalDate starDate, LocalDate endDate) {
        try {
            // Fetch current profit and loss reports
            List<ProfitLossReport> currentProfitAndLoss = fetchcurrentProfitAndLoss(userId, starDate, endDate);
            return transformProfitAndLoss(currentProfitAndLoss);
        } catch (Exception e) {
            logger.error("Error fetching total profit and loss for user: {}", userId, e);
            throw new ProfitLossServiceException("Error fetching total profit and loss", e);
        }
    }

}
