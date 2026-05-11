package com.cryptowatch.dto;

import com.cryptowatch.entity.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
    @NotNull(message = "Coin ID is required") Long coinId,
    @NotNull(message = "Transaction type is required") TransactionType type,
    @NotNull @DecimalMin(value = "0.00000001", message = "Quantity must be > 0") BigDecimal quantity,
    @NotNull @DecimalMin(value = "0.00000001", message = "Price must be > 0") BigDecimal price,
    @NotNull(message = "Date is required") LocalDate date
) {}
