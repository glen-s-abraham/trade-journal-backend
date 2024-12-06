package com.trading.tradejournal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.tradejournal.model.ProfitAndLoss;

public interface ProfitAndLossRepository extends JpaRepository<ProfitAndLoss, Long> {
    
}
