package com.taskapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank( message = "Name cannot be null or empty")
        String name,
        @NotBlank( message = "Email cannot be null or empty")
        @Email( message = "Email is not valid")
        String email,
        @NotBlank( message = "Password cannot be null or empty")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {
}
