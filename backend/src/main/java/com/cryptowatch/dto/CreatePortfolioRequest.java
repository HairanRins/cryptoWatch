package com.cryptowatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePortfolioRequest(
    @NotBlank @Size(min = 1, max = 100) String name
) {}
