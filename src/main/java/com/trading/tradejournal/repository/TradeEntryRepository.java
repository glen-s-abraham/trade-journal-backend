package com.trading.tradejournal.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trading.tradejournal.dto.profitLoss.ProfitLossTrendDto;
import com.trading.tradejournal.dto.trade.TradeEntryNetDto;
import com.trading.tradejournal.model.TradeEntry;

public interface TradeEntryRepository extends JpaRepository<TradeEntry, Long> {
        @Query("SELECT t FROM TradeEntry t WHERE t.userId = :userId")
        List<TradeEntry> findByUserId(String userId);

        Optional<TradeEntry> findByIdAndUserId(Long id, String userId);

        @Query("SELECT t FROM TradeEntry t WHERE t.userId=:userId AND t.tradeDate BETWEEN :startDate AND :endDate ")
        List<TradeEntry> findByUserIdAndTradeDateBetween(String userId, LocalDate startDate, LocalDate endDate);

        @Query("SELECT new com.trading.tradejournal.dto.trade.TradeEntryNetDto( " +
                        "t.stockSymbol, " +
                        "SUM(CASE WHEN t.tradeType = 'BUY' THEN t.quantity * t.price ELSE 0 END) / NULLIF(SUM(CASE WHEN t.tradeType = 'BUY' THEN t.quantity ELSE 0 END), 0), "
                        +
                        "SUM(CASE WHEN t.tradeType = 'BUY' THEN t.quantity ELSE 0 END) - SUM(CASE WHEN t.tradeType = 'SELL' THEN t.quantity ELSE 0 END)) "
                        +
                        "FROM TradeEntry t " +
                        "WHERE t.userId = :userId " +
                        "GROUP BY t.stockSymbol")
        List<TradeEntryNetDto> calculateNetQuantityAndAveragePrice(String userId);

        @Query("SELECT new com.trading.tradejournal.dto.trade.TradeEntryNetDto( " +
                        "t.stockSymbol, " +
                        "SUM(CASE WHEN t.tradeType = 'BUY' THEN t.quantity * t.price ELSE 0 END) / NULLIF(SUM(CASE WHEN t.tradeType = 'BUY' THEN t.quantity ELSE 0 END), 0), "
                        +
                        "SUM(CASE WHEN t.tradeType = 'BUY' THEN t.quantity ELSE 0 END) - SUM(CASE WHEN t.tradeType = 'SELL' THEN t.quantity ELSE 0 END)) "
                        +
                        "FROM TradeEntry t " +
                        "WHERE t.userId = :userId " +
                        "AND t.tradeDate BETWEEN :startDate AND :endDate " +
                        "GROUP BY t.stockSymbol")
        List<TradeEntryNetDto> calculateNetQuantityAndAveragePrice(String userId, LocalDate startDate,
                        LocalDate endDate);

        @Query("SELECT new com.trading.tradejournal.dto.trade.TradeEntryNetDto( " +
                        "t.stockSymbol, " +
                        "SUM(CASE WHEN t.tradeType = 'BUY' THEN t.quantity * t.price ELSE 0 END) / NULLIF(SUM(CASE WHEN t.tradeType = 'BUY' THEN t.quantity ELSE 0 END), 0), "
                        +
                        "SUM(CASE WHEN t.tradeType = 'BUY' THEN t.quantity ELSE 0 END) - SUM(CASE WHEN t.tradeType = 'SELL' THEN t.quantity ELSE 0 END)) "
                        +
                        "FROM TradeEntry t " +
                        "WHERE t.userId = :userId AND t.stockSymbol = :stockSymbol " +
                        "GROUP BY t.stockSymbol")
        Optional<TradeEntryNetDto> calculateNetQuantityAndAveragePriceForSymbol(String userId, String stockSymbol);

        @Query("SELECT new com.trading.tradejournal.dto.profitLoss.ProfitLossTrendDto("
                        + " t.tradeDate, "
                        + " SUM(CASE WHEN t.tradeType = 'SELL' THEN t.quantity * t.price ELSE 0 END) - "
                        + " SUM(CASE WHEN t.tradeType = 'BUY' THEN t.quantity * t.price ELSE 0 END) "
                        + ") "
                        + "FROM TradeEntry t "
                        + "WHERE t.userId = :userId "
                        + "GROUP BY t.tradeDate "
                        + "ORDER BY t.tradeDate")
        List<ProfitLossTrendDto> findDailyProfitLoss(@Param("userId") String userId);

        @Query(value = "SELECT EXTRACT(YEAR FROM t.trade_date) AS year, "
                        + "EXTRACT(WEEK FROM t.trade_date) AS week, "
                        + "SUM(CASE WHEN t.trade_type = 'SELL' THEN t.quantity * t.price ELSE 0 END) - "
                        + "SUM(CASE WHEN t.trade_type = 'BUY' THEN t.quantity * t.price ELSE 0 END) AS net_profit "
                        + "FROM trade_entries t "
                        + "WHERE t.user_id = :userId "
                        + "GROUP BY EXTRACT(YEAR FROM t.trade_date), EXTRACT(WEEK FROM t.trade_date) "
                        + "ORDER BY year, week", nativeQuery = true)
        List<Object[]> findWeeklyProfitLoss(@Param("userId") String userId);

        @Query(value = "SELECT EXTRACT(YEAR FROM t.trade_date) AS year, "
                        + "EXTRACT(MONTH FROM t.trade_date) AS month, "
                        + "SUM(CASE WHEN t.trade_type = 'SELL' THEN t.quantity * t.price ELSE 0 END) - "
                        + "SUM(CASE WHEN t.trade_type = 'BUY' THEN t.quantity * t.price ELSE 0 END) AS net_profit "
                        + "FROM trade_entries t "
                        + "WHERE t.user_id = :userId "
                        + "GROUP BY EXTRACT(YEAR FROM t.trade_date), EXTRACT(MONTH FROM t.trade_date) "
                        + "ORDER BY year, month", nativeQuery = true)
        List<Object[]> findMonthlyProfitLoss(@Param("userId") String userId);

}
