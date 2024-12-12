package com.trading.tradejournal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trading.tradejournal.dto.profitLoss.ProfitLossReport;
import com.trading.tradejournal.dto.profitLoss.ProfitLossTrendDto;
import com.trading.tradejournal.dto.profitLoss.TotalProfitAndLoss;
import com.trading.tradejournal.service.auth.AuthService;
import com.trading.tradejournal.service.profitLoss.ProfitAndLossService;
import com.trading.tradejournal.service.profitLoss.enums.ProfitAndLossInterval;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/profit-and-loss")
public class ProfitAndLossController {

    private final AuthService authService;
    private final ProfitAndLossService profitAndLossService;

    public ProfitAndLossController(AuthService authService, ProfitAndLossService profitAndLossService) {
        this.authService = authService;
        this.profitAndLossService = profitAndLossService;
    }

    @GetMapping("/report")
    public ResponseEntity<?> getProfitLossReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String userId = authService.getUserId();
            List<ProfitLossReport> report;
            if (startDate != null && endDate != null) {
                report = profitAndLossService.fetchcurrentProfitAndLoss(userId, startDate, endDate);
            } else {
                report = profitAndLossService.fetchcurrentProfitAndLoss(userId);
            }
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching profit and loss report");
        }

    }

    @GetMapping("/cumulative")
    public ResponseEntity<?> getTotoalProfitAndLoss(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String userId = authService.getUserId();
            TotalProfitAndLoss totalProfitAndLoss;
            if (startDate != null && endDate != null) {
                totalProfitAndLoss = profitAndLossService.fetchTotalProfitAndLoss(userId, startDate, endDate);
            } else {
                totalProfitAndLoss = profitAndLossService.fetchTotalProfitAndLoss(userId);
            }
            return ResponseEntity.ok(totalProfitAndLoss);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching profit and loss report");
        }

    }

    @GetMapping("/trend")
    public ResponseEntity<?> getProfitLossTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam ProfitAndLossInterval interval) {
        try {
            String userId = authService.getUserId();
            List<ProfitLossTrendDto> trend = profitAndLossService.getProfitLossTrend(userId, startDate, endDate,
                    interval);
            return ResponseEntity.ok(trend);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching profit and loss trend");
        }
    }

}
