package com.taskapp.boardtask.dto.response;

import com.taskapp.board.dto.response.BoardMemberResponse;
import com.taskapp.boardtask.entity.enums.BoardTaskPriority;
import com.taskapp.boardtask.entity.enums.BoardTaskState;
import com.taskapp.comment.dto.response.CommentResponse;
import com.taskapp.subtask.dto.response.SubTaskResponse;

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
