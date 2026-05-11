package com.cryptowatch.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CoinDto(
    Long id, String name, String symbol, String coinId,
    String imageUrl, BigDecimal currentPrice, BigDecimal marketCap,
    BigDecimal change24h, BigDecimal volume24h, LocalDateTime lastUpdated
) {}
