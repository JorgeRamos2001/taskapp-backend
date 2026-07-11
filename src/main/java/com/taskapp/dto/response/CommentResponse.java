package com.taskapp.dto.response;

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
