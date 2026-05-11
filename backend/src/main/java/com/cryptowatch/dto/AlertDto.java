package com.cryptowatch.dto;

import com.cryptowatch.entity.AlertCondition;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AlertDto(
    Long id, CoinDto coin, Long portfolioId, BigDecimal targetPrice,
    AlertCondition condition, boolean active, LocalDateTime triggeredAt,
    LocalDateTime createdAt
) {}
