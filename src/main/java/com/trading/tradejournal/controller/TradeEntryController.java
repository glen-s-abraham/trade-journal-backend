package com.trading.tradejournal.controller;

import org.springframework.web.bind.annotation.RestController;

import com.trading.tradejournal.dto.profitLoss.ProfitLossModificationDto;
import com.trading.tradejournal.dto.trade.TradeEntryDto;
import com.trading.tradejournal.dto.trade.TradeEntryModificationDto;
import com.trading.tradejournal.dto.trade.TradeEntryNetDto;
import com.trading.tradejournal.exception.auth.UnauthorizedException;
import com.trading.tradejournal.exception.profitLoss.ProfitLossServiceException;
import com.trading.tradejournal.exception.trade.TradeEntryNotFoundException;
import com.trading.tradejournal.model.TradeType;
import com.trading.tradejournal.service.auth.AuthService;
import com.trading.tradejournal.service.profitLoss.ProfitAndLossService;
import com.trading.tradejournal.service.trade.TradeEntryService;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/trade")
public class TradeEntryController {

    private final TradeEntryService tradeEntryService;
    private final ProfitAndLossService profitAndLossService;
    private final AuthService authService;

    public TradeEntryController(TradeEntryService tradeEntryService, ProfitAndLossService profitAndLossService,
            AuthService authService) {
        this.tradeEntryService = tradeEntryService;
        this.profitAndLossService = profitAndLossService;
        this.authService = authService;
    }

    private String getAuthenticatedUserId() {
        String userId = authService.getUserId();
        if (userId == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return userId;
    }

    /**
     * Create a new trade entry.
     *
     * @param tradeEntry The trade entry modification DTO containing the details for
     *                   the new trade.
     * @return The created trade entry DTO.
     */
    @PostMapping("/")
    public ResponseEntity<?> createTradeEntry(@RequestBody TradeEntryModificationDto tradeEntry) {
        TradeEntryDto tradeEntryDto = null;
        try {
            String userId = getAuthenticatedUserId();
            TradeEntryModificationDto modifiedTradeEntry = tradeEntry.withUserId(userId);
            tradeEntryDto = tradeEntryService.createTradeEntry(modifiedTradeEntry);
            if (tradeEntryDto.tradeType() == TradeType.SELL) {
                //todo average proce calculation function to be added
                ProfitLossModificationDto profitLossModificationDto = new ProfitLossModificationDto(userId,
                        tradeEntryDto.stockSymbol(), tradeEntryDto.tradeDate(), tradeEntryDto.price(),
                        tradeEntryDto.quantity(), 0d);
                profitAndLossService.createProfitLoss(profitLossModificationDto, tradeEntryDto);
            }
            return ResponseEntity.ok(tradeEntryDto);
        } catch (ProfitLossServiceException pe) {
            // Rollback the trade entry if profit/loss creation fails
            if (tradeEntryDto != null && tradeEntryDto.id() != null) {
                tradeEntryService.deleteTradeEntryById(tradeEntryDto.id());
            }
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
                    .body(Map.of("error", "Failed to create profit/loss", "details", pe.getMessage()));

        } catch (Exception e) {
            // Log and return a generic error response
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
                    .body(Map.of("error", "An unexpected error occurred", "details", e.getMessage()));
        }

    }

    /**
     * Fetch all trade entries.
     *
     * @return List of trade entry DTOs.
     */
    @GetMapping
    public ResponseEntity<?> fetchAllTrades() {
        try {
            String userId = getAuthenticatedUserId();
            List<TradeEntryDto> tradeEntries = tradeEntryService.fetchTradeEntriesByUserId(userId);
            return ResponseEntity.ok(tradeEntries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred", "details", e.getMessage()));
        }
    }

    /**
     * Fetch Net positions.
     *
     * @return List of net position DTOs.
     */
    @GetMapping("/net")
    public ResponseEntity<?> fetchNetPositions() {
        try {
            String userId = getAuthenticatedUserId();
            List<TradeEntryNetDto> tradeEntries = tradeEntryService.fetchNetPositions(userId);
            return ResponseEntity.ok(tradeEntries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred", "details", e.getMessage()));
        }
    }

    /**
     * Fetch a trade entry by ID.
     *
     * @param id The ID of the trade entry to fetch.
     * @return The trade entry DTO if found, or 404 status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> fetchTradeById(@PathVariable Long id) {
        try {
            String userId = getAuthenticatedUserId();
            TradeEntryDto tradeEntry = tradeEntryService.fetchTradeEntryByIdAndUserId(id, userId);
            return ResponseEntity.ok(tradeEntry);
        } catch (TradeEntryNotFoundException e) {
            // Handle trade not found exception
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                    .body(Map.of("error", "Trade entry not found", "details", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred", "details", e.getMessage()));
        }
    }

    /**
     * Update an existing trade entry.
     *
     * @param id         The ID of the trade entry to update.
     * @param tradeEntry The trade entry modification DTO with updated details.
     * @return The updated trade entry DTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrade(@PathVariable Long id,
            @RequestBody TradeEntryModificationDto tradeEntry) {
        try {
            String userId = getAuthenticatedUserId();
            TradeEntryDto updatedTrade = tradeEntryService.updateTradeEntryForUser(id, userId, tradeEntry);
            return ResponseEntity.ok(updatedTrade);
        } catch (TradeEntryNotFoundException e) {
            // Handle trade not found exception
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                    .body(Map.of("error", "Trade entry not found", "details", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred", "details", e.getMessage()));
        }
    }

    /**
     * Delete a trade entry by ID.
     *
     * @param id The ID of the trade entry to delete.
     * @return Response entity with appropriate status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrade(@PathVariable Long id) {
        try {
            String userId = getAuthenticatedUserId();
            tradeEntryService.deleteTradeEntryByIdForUser(id, userId);
            return ResponseEntity.noContent().build();
        } catch (TradeEntryNotFoundException e) {
            // Handle trade not found exception
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                    .body(Map.of("error", "Trade entry not found", "details", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred", "details", e.getMessage()));
        }
    }

}
