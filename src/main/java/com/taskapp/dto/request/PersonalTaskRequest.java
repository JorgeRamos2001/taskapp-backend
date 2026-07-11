package com.taskapp.dto.request;

import com.taskapp.entity.enums.PersonalTaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PersonalTaskRequest(
        @NotBlank( message = "Title cannot be null or empty")
        String title,
        String description,
        @NotNull( message = "Priority cannot be null")
        PersonalTaskPriority priority,
        LocalDateTime dueDate
) {
}
