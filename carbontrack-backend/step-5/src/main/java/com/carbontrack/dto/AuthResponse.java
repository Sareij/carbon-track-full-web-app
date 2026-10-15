package com.carbontrack.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMs,
        ProfileResponse user
) {}
