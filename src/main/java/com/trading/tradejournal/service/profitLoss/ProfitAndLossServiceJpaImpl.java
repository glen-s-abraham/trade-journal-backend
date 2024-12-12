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
import com.trading.tradejournal.dto.profitLoss.ProfitLossTrendDto;
import com.trading.tradejournal.dto.profitLoss.TotalProfitAndLoss;
import com.trading.tradejournal.dto.stock.StockPrice;
import com.trading.tradejournal.dto.trade.TradeEntryDto;
import com.trading.tradejournal.dto.trade.TradeEntryNetDto;
import com.trading.tradejournal.exception.profitLoss.ProfitLossServiceException;
import com.trading.tradejournal.model.ProfitAndLoss;
import com.trading.tradejournal.model.TradeEntry;
import com.trading.tradejournal.repository.ProfitAndLossRepository;
import com.trading.tradejournal.repository.TradeEntryRepository;
import com.trading.tradejournal.service.profitLoss.enums.ProfitAndLossInterval;
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

    @Override
    public List<ProfitLossReport> fetchcurrentProfitAndLoss(String userId) {
        try {
            List<TradeEntryNetDto> netPositions = tradeEntryRepository.calculateNetQuantityAndAveragePrice(userId);
            List<ProfitLossReport> profitLossStatus = ProfitAndLossTransformUtils.transformNetPositions(netPositions,
                    stockService);
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
            List<ProfitLossReport> profitLossStatus = ProfitAndLossTransformUtils.transformNetPositions(netPositions,
                    stockService);
            return profitLossStatus;
        } catch (Exception e) {
            throw new ProfitLossServiceException("Error fetching current profit and loss");
        }
    }

    @Override
    public TotalProfitAndLoss fetchTotalProfitAndLoss(String userId) {
        try {
            // Fetch current profit and loss reports
            List<ProfitLossReport> currentProfitAndLoss = fetchcurrentProfitAndLoss(userId);
            return ProfitAndLossTransformUtils.transformProfitAndLoss(currentProfitAndLoss);
        } catch (Exception e) {
            logger.error("Error fetching total profit and loss for user: {}", userId, e);
            throw new ProfitLossServiceException("Error fetching total profit and loss", e);
        }
    }

    @Override
    public TotalProfitAndLoss fetchTotalProfitAndLoss(String userId, LocalDate startDate, LocalDate endDate) {
        try {
            // Fetch current profit and loss reports
            List<ProfitLossReport> currentProfitAndLoss = fetchcurrentProfitAndLoss(userId, startDate, endDate);
            return ProfitAndLossTransformUtils.transformProfitAndLoss(currentProfitAndLoss);
        } catch (Exception e) {
            logger.error("Error fetching total profit and loss for user: {}", userId, e);
            throw new ProfitLossServiceException("Error fetching total profit and loss", e);
        }
    }

    @Override
    public List<ProfitLossTrendDto> getProfitLossTrend(String userId, LocalDate startDate, LocalDate endDate,
            ProfitAndLossInterval interval) {
        try {
            List<ProfitLossTrendDto> profitAndLossTrend;

            switch (interval) {
                case DAILY:
                    profitAndLossTrend = tradeEntryRepository.findDailyProfitLoss(userId, startDate, endDate);
                    break;

                case WEEKLY:
                    List<Object[]> weeklyResults = tradeEntryRepository.findWeeklyProfitLoss(userId, startDate,
                            endDate);
                    profitAndLossTrend = ProfitAndLossTransformUtils.transformWeeklyResults(weeklyResults);
                    break;

                case MONTHLY:
                    List<Object[]> monthlyResults = tradeEntryRepository.findMonthlyProfitLoss(userId, startDate,
                            endDate);
                    profitAndLossTrend = ProfitAndLossTransformUtils.transformMonthlyResults(monthlyResults);
                    break;

                default:
                    throw new ProfitLossServiceException("Unknown aggregation type: " + interval);
            }

            return profitAndLossTrend;

        } catch (ProfitLossServiceException e) {
            logger.warn("Validation error for profit and loss trend. User: {}, Interval: {}, Start: {}, End: {}",
                    userId, interval, startDate, endDate, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching profit and loss trend for user: {}, Interval: {}, Start: {}, End: {}",
                    userId, interval, startDate, endDate, e);
            throw new ProfitLossServiceException("Error fetching profit and loss trend", e);
        }
    }

}
