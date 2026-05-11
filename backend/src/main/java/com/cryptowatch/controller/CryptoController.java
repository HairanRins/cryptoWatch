package com.cryptowatch.controller;

import com.cryptowatch.dto.ApiResponse;
import com.cryptowatch.dto.CoinDto;
import com.cryptowatch.dto.PriceHistoryDto;
import com.cryptowatch.service.ChartService;
import com.cryptowatch.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CryptoController {
    private final CoinService coinService;
    private final ChartService chartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CoinDto>>> getTopCoins() {
        return ResponseEntity.ok(ApiResponse.success(coinService.getTopCoins()));
    }

    @GetMapping("/{coinId}")
    public ResponseEntity<ApiResponse<CoinDto>> getCoinById(@PathVariable String coinId) {
        return coinService.getCoinById(coinId)
            .map(coin -> ResponseEntity.ok(ApiResponse.success(coin)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{coinId}/history")
    public ResponseEntity<ApiResponse<PriceHistoryDto>> getPriceHistory(
            @PathVariable String coinId, @RequestParam(defaultValue = "7d") String period) {
        return ResponseEntity.ok(ApiResponse.success(chartService.getPriceHistory(coinId, period)));
    }
}
