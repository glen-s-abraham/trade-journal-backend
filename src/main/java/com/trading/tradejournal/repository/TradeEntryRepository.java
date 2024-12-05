package com.trading.tradejournal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.tradejournal.model.TradeEntry;

public interface TradeEntryRepository extends JpaRepository<TradeEntry, Long> {

}
