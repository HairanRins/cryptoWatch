package com.cryptowatch.controller;

import com.cryptowatch.dto.AlertDto;
import com.cryptowatch.dto.ApiResponse;
import com.cryptowatch.dto.CreateAlertRequest;
import com.cryptowatch.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/alerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertController {
    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertDto>>> getAlerts(@PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success(alertService.getAlertsByPortfolio(portfolioId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AlertDto>> createAlert(
            @PathVariable Long portfolioId, @Valid @RequestBody CreateAlertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Alert created", alertService.createAlert(portfolioId, request)));
    }
}
