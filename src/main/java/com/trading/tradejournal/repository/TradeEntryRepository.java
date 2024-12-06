package com.trading.tradejournal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.trading.tradejournal.model.TradeEntry;

public interface TradeEntryRepository extends JpaRepository<TradeEntry, Long> {
    @Query("SELECT t FROM TradeEntry t WHERE t.userId = :userId")
    List<TradeEntry> findByUserId(String userId);

    Optional<TradeEntry> findByIdAndUserId(Long id, String userId);

}
