package com.taskapp.dto.response;

import com.taskapp.entity.enums.BoardTaskPriority;
import com.taskapp.entity.enums.BoardTaskState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BoardTaskResponse(
        UUID id,
        UUID boardId,
        BoardMemberResponse assignee,
        String title,
        String description,
        BoardTaskPriority priority,
        BoardTaskState state,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        List<SubTaskResponse> subTasks,
        List<CommentResponse> comments
) {
}
