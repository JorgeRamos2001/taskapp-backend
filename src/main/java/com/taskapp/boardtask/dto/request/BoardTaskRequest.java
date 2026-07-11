package com.taskapp.boardtask.dto.request;

import com.taskapp.boardtask.entity.enums.BoardTaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record BoardTaskRequest(
        @NotNull( message = "Board id cannot be null")
        UUID boardId,
        UUID assigneeId,
        @NotBlank( message = "Title cannot be null or empty")
        String title,
        String description,
        @NotNull( message = "Priority cannot be null")
        BoardTaskPriority priority,
        LocalDateTime dueDate
) {
}
