package com.taskapp.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank( message = "Refresh token cannot be null or empty")
        String refreshToken
) {
}
