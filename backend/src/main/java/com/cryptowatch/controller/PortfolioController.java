package com.cryptowatch.controller;

import com.cryptowatch.dto.*;
import com.cryptowatch.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PortfolioController {

    private final PortfolioService portfolioService;

    private UUID getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        return UUID.fromString((String) authentication.getPrincipal());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PortfolioDto>>> getAllPortfolios(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        List<PortfolioDto> portfolios = portfolioService.getPortfoliosByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(portfolios));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioDto>> getPortfolio(@PathVariable Long id, Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        PortfolioDto portfolio = portfolioService.getPortfolio(id, userId);
        return ResponseEntity.ok(ApiResponse.success(portfolio));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioDto>> createPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request,
            Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        PortfolioDto portfolio = portfolioService.createPortfolio(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Portfolio créé", portfolio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(@PathVariable Long id, Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        portfolioService.deletePortfolio(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Portfolio supprimé", null));
    }

    @PostMapping("/{portfolioId}/transactions")
    public ResponseEntity<ApiResponse<TransactionDto>> addTransaction(
            @PathVariable Long portfolioId,
            @Valid @RequestBody CreateTransactionRequest request,
            Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        TransactionDto transaction = portfolioService.addTransaction(portfolioId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Transaction ajoutée", transaction));
    }

    @DeleteMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long transactionId) {
        portfolioService.deleteTransaction(transactionId);
        return ResponseEntity.ok(ApiResponse.success("Transaction supprimée", null));
    }
}
