package com.trading.tradejournal.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trading.tradejournal.dto.stocks.StockPrice;
import com.trading.tradejournal.exception.StockServiceException;
import com.trading.tradejournal.service.StockService;

@RestController
@RequestMapping("/stock")
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);

    private final StockService stockService;

    public StockController(@Qualifier("AlphaVantage") StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/price")
    public ResponseEntity<StockPrice> getStockPrice(@RequestParam String symbol) {
        try {
            logger.info("Fetching stock price for symbol: {}", symbol);
            BigDecimal price = stockService.fetchStockPrice(symbol);
            StockPrice stockPrice = new StockPrice(symbol, price);
            return ResponseEntity.ok(stockPrice);
        } catch (StockServiceException se) {
            return ResponseEntity.badRequest().body(new StockPrice(symbol, null));
        }
    }
}
