package com.taskapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank( message = "Refresh token cannot be null or empty")
        String refreshToken
) {
}
