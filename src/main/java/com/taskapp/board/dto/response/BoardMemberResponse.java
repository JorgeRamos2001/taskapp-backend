package com.taskapp.board.dto.response;

import com.taskapp.board.entity.enums.BoardMemberRole;

import java.util.UUID;

public record BoardMemberResponse(
        UUID id,
        String name,
        String email,
        String urlAvatar,
        BoardMemberRole role
) {
}
