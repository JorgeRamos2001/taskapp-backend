package com.taskapp.board.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BoardResponse(
        UUID id,
        String owner,
        String title,
        String description,
        LocalDateTime createdAt,
        List<BoardMemberResponse> members
) {
}
