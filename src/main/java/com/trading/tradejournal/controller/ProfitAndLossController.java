package com.trading.tradejournal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trading.tradejournal.dto.profitLoss.ProfitLossReport;
import com.trading.tradejournal.dto.profitLoss.TotalProfitAndLoss;
import com.trading.tradejournal.service.auth.AuthService;
import com.trading.tradejournal.service.profitLoss.ProfitAndLossService;

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
            }else{
                report = profitAndLossService.fetchcurrentProfitAndLoss(userId);
            }
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching profit and loss report");
        }

    }

    @GetMapping("/cumulative")
    public ResponseEntity<?> getTotoalProfitAndLoss() {
        try {
            String userId = authService.getUserId();
            TotalProfitAndLoss totalProfitAndLoss = profitAndLossService.fetchTotalProfitAndLoss(userId);
            return ResponseEntity.ok(totalProfitAndLoss);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching profit and loss report");
        }

    }

}
