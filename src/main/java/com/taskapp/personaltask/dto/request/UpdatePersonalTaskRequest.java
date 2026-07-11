package com.taskapp.personaltask.dto.request;

import com.taskapp.personaltask.entity.enums.PersonalTaskPriority;
import com.taskapp.personaltask.entity.enums.PersonalTaskState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdatePersonalTaskRequest(
        @NotBlank( message = "Title cannot be null or empty")
        String title,
        String description,
        @NotNull( message = "Priority cannot be null")
        PersonalTaskPriority priority,
        @NotNull( message = "State cannot be null")
        PersonalTaskState state,
        LocalDateTime dueDate
) {
}
