package com.taskapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank( message = "Old password cannot be null or empty")
        @Size(min = 6, message = "Old password must be at least 6 characters long")
        String oldPassword,
        @NotBlank( message = "New password cannot be null or empty")
        @Size(min = 6, message = "New password must be at least 6 characters long")
        String newPassword
) {
}
