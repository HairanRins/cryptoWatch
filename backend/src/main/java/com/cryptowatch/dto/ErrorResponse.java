package com.cryptowatch.dto;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ErrorResponse(
    int status, String error, String message,
    List<String> details, String path, LocalDateTime timestamp
) {}
