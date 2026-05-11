package com.cryptowatch.dto;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record PortfolioAssetDto(
    Long coinId, String coinName, String coinSymbol, String coinImageUrl,
    BigDecimal quantity, BigDecimal avgBuyPrice, BigDecimal currentPrice,
    BigDecimal currentValue, BigDecimal invested, BigDecimal pnl,
    BigDecimal pnlPercent, BigDecimal portfolioPercent
) {}
