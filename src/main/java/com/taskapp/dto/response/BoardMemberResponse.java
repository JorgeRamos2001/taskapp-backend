package com.taskapp.dto.response;

import com.taskapp.entity.enums.BoardMemberRole;

import java.util.UUID;

public record BoardMemberResponse(
        UUID id,
        String name,
        String email,
        String urlAvatar,
        BoardMemberRole role
) {
}
