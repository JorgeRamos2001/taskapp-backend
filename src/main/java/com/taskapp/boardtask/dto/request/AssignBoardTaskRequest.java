package com.taskapp.boardtask.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignBoardTaskRequest(
        @NotNull( message = "Board id cannot be null")
        UUID boardId,
        @NotNull( message = "User id cannot be null")
        UUID userId
) {
}
