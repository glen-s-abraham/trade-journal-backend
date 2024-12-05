package com.trading.tradejournal.controller;

import org.springframework.web.bind.annotation.RestController;

import com.trading.tradejournal.dto.trade.TradeEntryDto;
import com.trading.tradejournal.dto.trade.TradeEntryModificationDto;
import com.trading.tradejournal.service.trade.TradeEntryService;

import java.util.List;

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

    public TradeEntryController(TradeEntryService tradeEntryService) {
        this.tradeEntryService = tradeEntryService;
    }

    /**
     * Create a new trade entry.
     *
     * @param tradeEntry The trade entry modification DTO containing the details for
     *                   the new trade.
     * @return The created trade entry DTO.
     */
    @PostMapping("/")
    public ResponseEntity<TradeEntryDto> postMethodName(@RequestBody TradeEntryModificationDto tradeEntry) {
        try {
            TradeEntryDto tradeEntryDto = tradeEntryService.createTradeEntry(tradeEntry);
            return ResponseEntity.ok(tradeEntryDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

    }

    /**
     * Fetch all trade entries.
     *
     * @return List of trade entry DTOs.
     */
    @GetMapping
    public ResponseEntity<List<TradeEntryDto>> fetchAllTrades() {
        try {
            List<TradeEntryDto> tradeEntries = tradeEntryService.fetchTradeEntries();
            return ResponseEntity.ok(tradeEntries);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Fetch a trade entry by ID.
     *
     * @param id The ID of the trade entry to fetch.
     * @return The trade entry DTO if found, or 404 status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TradeEntryDto> fetchTradeById(@PathVariable Long id) {
        try {
            TradeEntryDto tradeEntry = tradeEntryService.fetchTradeEntryById(id);
            return ResponseEntity.ok(tradeEntry);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
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
    public ResponseEntity<TradeEntryDto> updateTrade(@PathVariable Long id,
            @RequestBody TradeEntryModificationDto tradeEntry) {
        try {
            TradeEntryDto updatedTrade = tradeEntryService.updateTradeEntry(id, tradeEntry);
            return ResponseEntity.ok(updatedTrade);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Delete a trade entry by ID.
     *
     * @param id The ID of the trade entry to delete.
     * @return Response entity with appropriate status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        try {
            tradeEntryService.deleteTradeEntryById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
