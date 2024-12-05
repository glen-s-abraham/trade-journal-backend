package com.trading.tradejournal.service;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.tradejournal.config.AlphaVantageProperties;
import com.trading.tradejournal.exception.StockServiceException;

@Service
@Qualifier(value = "AlphaVantage")
public class StockServiceAVimpl implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceYfinanceImpl.class);

    private static final String STOCK_PRICE_NOT_AVAILABLE = "Stock price not available for symbol: ";
    private static final String UNABLE_TO_FETCH_STOCK = "Unable to fetch details for stock symbol: ";

    private static final String DAILY_QUOTE_API_STRING = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&outputsize=compact&apikey=%s";

    private final RestTemplate restTemplate;
    private final AlphaVantageProperties properties;
    private final ObjectMapper objectMapper;

    public StockServiceAVimpl(RestTemplate restTemplate, AlphaVantageProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Utility method to validate objects and JSON nodes.
     *
     * @param obj          The object to validate.
     * @param errorMessage The error message if validation fails.
     * @return The validated object.
     */
    private <T> T requireNonNull(T obj, String errorMessage) {
        if (obj == null || (obj instanceof JsonNode && ((JsonNode) obj).isMissingNode()))
            throw new StockServiceException(errorMessage);
        return obj;
    }

    @Override
    public BigDecimal fetchStockPrice(String symbol) throws StockServiceException {

        try {
            String url = String.format(
                    DAILY_QUOTE_API_STRING,
                    symbol, properties.getKey());
            logger.info("Fetching stock price for symbol: {}", symbol);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode rootNode = requireNonNull(objectMapper.readTree(response), UNABLE_TO_FETCH_STOCK + symbol);

            JsonNode timeSeriesNode = requireNonNull(rootNode.path("Time Series (Daily)"),
                    STOCK_PRICE_NOT_AVAILABLE + symbol);
            if (timeSeriesNode.isEmpty()) {
                throw new StockServiceException(STOCK_PRICE_NOT_AVAILABLE + symbol);
            }

            Map.Entry<String, JsonNode> latestEntry = timeSeriesNode.fields().next();
            String latestClose = requireNonNull(latestEntry.getValue().path("4. close").asText(),
                    STOCK_PRICE_NOT_AVAILABLE);

            logger.info("Successfully fetched the latest stock price for symbol {}: {}", symbol, latestClose);
            return new BigDecimal(latestClose);

        } catch (Exception e) {
            logger.error("Error fetching stock price for symbol: {}", symbol, e);
            throw new StockServiceException(UNABLE_TO_FETCH_STOCK + symbol, e);
        }
    }

}
