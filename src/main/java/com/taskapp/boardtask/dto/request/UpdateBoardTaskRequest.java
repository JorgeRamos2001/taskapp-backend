package com.taskapp.boardtask.dto.request;

import com.taskapp.boardtask.entity.enums.BoardTaskPriority;
import com.taskapp.boardtask.entity.enums.BoardTaskState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateBoardTaskRequest(
        @NotNull( message = "Board id cannot be null")
        UUID boardId,
        @NotBlank( message = "Title cannot be null or empty")
        String title,
        String description,
        @NotNull( message = "Priority cannot be null")
        BoardTaskPriority priority,
        @NotNull( message = "State cannot be null")
        BoardTaskState state,
        LocalDateTime dueDate
) {
}
