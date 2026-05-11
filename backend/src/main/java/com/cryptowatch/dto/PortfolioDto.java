package com.cryptowatch.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PortfolioDto(
    Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt,
    BigDecimal totalValue, BigDecimal totalInvested, BigDecimal totalPnL,
    BigDecimal totalPnLPercent, List<PortfolioAssetDto> assets,
    List<TransactionDto> transactions
) {}
