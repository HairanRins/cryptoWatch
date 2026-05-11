package com.cryptowatch.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record PriceHistoryDto(
    String coinId, String coinName, String coinSymbol,
    String period, List<PricePointDto> prices
) {}
