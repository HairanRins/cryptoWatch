package com.cryptowatch.dto;

import com.cryptowatch.entity.TransactionType;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record TransactionDto(
    Long id, Long portfolioId, CoinDto coin, TransactionType type,
    BigDecimal quantity, BigDecimal price, BigDecimal totalValue,
    LocalDate date, LocalDateTime createdAt
) {}
