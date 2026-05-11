package com.cryptowatch.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PricePointDto(LocalDateTime timestamp, BigDecimal price) {}
