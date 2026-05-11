package com.cryptowatch.controller;

import com.cryptowatch.dto.*;
import com.cryptowatch.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioDto>> getPortfolio(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getPortfolio(id)));
    }

    @PostMapping("/{portfolioId}/transactions")
    public ResponseEntity<ApiResponse<TransactionDto>> addTransaction(
            @PathVariable Long portfolioId, @Valid @RequestBody CreateTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Transaction added", portfolioService.addTransaction(portfolioId, request)));
    }
}
