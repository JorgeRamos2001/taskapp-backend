package com.taskapp.board.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BoardRequest(
        @NotBlank( message = "Title cannot be null or empty")
        String title,
        String description
) {
}
