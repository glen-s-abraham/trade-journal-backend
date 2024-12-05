package com.trading.tradejournal.service;

import java.io.IOException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.trading.tradejournal.exception.StockFetchException;
import com.trading.tradejournal.exception.StockServiceException;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Service
public class StockServiceYfinanceImpl implements StockService {
    private static final Logger logger = LoggerFactory.getLogger(StockServiceYfinanceImpl.class);

    private static final String STOCK_NOT_FOUND = "Stock details not found for symbol: ";
    private static final String STOCK_PRICE_NOT_AVAILABLE = "Stock price not available for symbol: ";
    private static final String UNABLE_TO_FETCH_STOCK = "Unable to fetch details for stock symbol: ";

    private <T> T requireNonNull(T obj, String errorMessage) {
        if (obj == null)
            throw new StockServiceException(errorMessage);
        return obj;
    }

    /**
     * Fetches stock details for the given symbol.
     *
     * @param symbol the stock symbol
     * @return the Stock object containing details of the stock
     * @throws StockFetchException if unable to fetch stock details
     */
    private Stock fetchStockDetails(String symbol) throws StockFetchException {
        try {
            logger.debug("Fetching stock details [class=StockServiceImpl, method=fetchStockDetails, symbol={}]",
                    symbol);
            Stock stock = requireNonNull(YahooFinance.get(symbol), STOCK_NOT_FOUND + symbol);
            return stock;

        } catch (Exception ex) {
            throw new StockFetchException("Error fetching stock details for symbol: " + symbol, ex);
        }
    }

    /**
     * Retrieves the stock price for the given symbol.
     *
     * @param symbol the stock symbol
     * @return the current stock price as a BigDecimal
     * @throws StockServiceException if there is an error fetching stock price
     */
    @Override
    public BigDecimal fetchStockPrice(String symbol) throws StockServiceException {
        try {
            logger.debug("Fetching stock details [class=StockServiceImpl, method=fetchStockPrice, symbol={}]", symbol);
            Stock stock = fetchStockDetails(symbol);
            BigDecimal price = requireNonNull(stock.getQuote().getPrice(), STOCK_PRICE_NOT_AVAILABLE + symbol);
            if (price == null) {
                logger.warn("Stock price is not available for symbol: {}", symbol);
                throw new StockServiceException(STOCK_PRICE_NOT_AVAILABLE + symbol);
            }
            logger.info("Retrieved stock price for symbol {}: {}", symbol, price);
            return price;
        } catch (StockFetchException sfe) {
            throw new StockServiceException(UNABLE_TO_FETCH_STOCK + symbol, sfe);
        } catch (Exception ex) {
            throw new StockServiceException("Unexpected error retrieving stock price for symbol: " + symbol, ex);
        }
    }

}
