package com.trading.tradejournal.service.profitLoss;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trading.tradejournal.dto.profitLoss.ProfitLossReport;
import com.trading.tradejournal.dto.profitLoss.ProfitLossTrendDto;
import com.trading.tradejournal.dto.profitLoss.TotalProfitAndLoss;
import com.trading.tradejournal.dto.trade.TradeEntryNetDto;
import com.trading.tradejournal.service.stock.StockService;

public class ProfitAndLossTransformUtils {
    private static final Logger logger = LoggerFactory.getLogger(ProfitAndLossTransformUtils.class);

    public static List<ProfitLossReport> transformNetPositions(List<TradeEntryNetDto> netPositions,
            StockService stockService) {
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

    public static TotalProfitAndLoss transformProfitAndLoss(List<ProfitLossReport> profitLossReport) {
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

    public static List<ProfitLossTrendDto> transformWeeklyResults(List<Object[]> results) {
        return results.stream()
                .map(row -> {
                    int year = ((Number) row[0]).intValue();
                    int week = ((Number) row[1]).intValue();
                    double netProfit = ((Number) row[2]).doubleValue();
                    LocalDate date = LocalDate.of(year, 1, 1).plusWeeks(week - 1);
                    return new ProfitLossTrendDto(date, netProfit);
                })
                .collect(Collectors.toList());
    }

    public static List<ProfitLossTrendDto> transformMonthlyResults(List<Object[]> results) {
        return results.stream()
                .map(row -> {
                    int year = ((Number) row[0]).intValue();
                    int month = ((Number) row[1]).intValue();
                    double netProfit = ((Number) row[2]).doubleValue();
                    LocalDate date = LocalDate.of(year, month, 1);
                    return new ProfitLossTrendDto(date, netProfit);
                })
                .collect(Collectors.toList());
    }
}
