package com.taskapp.personaltask.dto.response;

import com.taskapp.personaltask.entity.enums.PersonalTaskPriority;
import com.taskapp.personaltask.entity.enums.PersonalTaskState;

import java.time.LocalDateTime;
import java.util.UUID;

public record PersonalTaskResponse(
        UUID id,
        UUID owner,
        String title,
        String description,
        PersonalTaskPriority priority,
        PersonalTaskState state,
        LocalDateTime dueDate,
        LocalDateTime createdAt
) {
}
