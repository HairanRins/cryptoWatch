package com.cryptowatch.dto;

import com.cryptowatch.entity.AlertCondition;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateAlertRequest(
    @NotNull Long coinId,
    @NotNull @DecimalMin(value = "0.00000001") BigDecimal targetPrice,
    @NotNull AlertCondition condition
) {}
