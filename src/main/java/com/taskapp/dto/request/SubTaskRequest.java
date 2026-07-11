package com.taskapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SubTaskRequest(
        @NotBlank( message = "Title cannot be null or empty")
        String title
) {
}
