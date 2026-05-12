package com.cryptowatch.dto;

public record AuthResponse(
    String token,
    UserDto user
) {}
