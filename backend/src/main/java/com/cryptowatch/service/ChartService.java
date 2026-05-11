package com.cryptowatch.service;

import com.cryptowatch.client.CoinGeckoClient;
import com.cryptowatch.dto.PriceHistoryDto;
import com.cryptowatch.entity.Coin;
import com.cryptowatch.exception.ResourceNotFoundException;
import com.cryptowatch.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartService {
    private final CoinGeckoClient coinGeckoClient;
    private final CoinRepository coinRepository;
    private static final String DEFAULT_CURRENCY = "usd";

    public PriceHistoryDto getPriceHistory(String coinId, String period) {
        Coin coin = coinRepository.findByCoinId(coinId)
            .orElseThrow(() -> new ResourceNotFoundException("Coin not found: " + coinId));
        int days = switch (period.toLowerCase()) {
            case "1d", "1j" -> 1; case "7d", "7j" -> 7; case "30d", "1m" -> 30;
            case "90d", "3m" -> 90; case "365d", "1y", "1a" -> 365; case "max" -> 1825;
            default -> 7;
        };
        return PriceHistoryDto.builder().coinId(coinId).coinName(coin.getName())
            .coinSymbol(coin.getSymbol()).period(period)
            .prices(coinGeckoClient.getPriceHistory(coinId, days, DEFAULT_CURRENCY)).build();
    }
}
