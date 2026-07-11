package com.taskapp.comment.dto.response;

import com.taskapp.board.dto.response.BoardMemberResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        BoardMemberResponse user,
        UUID boardTaskId,
        String content,
        LocalDateTime createdAt
) {
}
