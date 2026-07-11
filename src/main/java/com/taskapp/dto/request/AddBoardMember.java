package com.taskapp.dto.request;

import com.taskapp.entity.enums.BoardMemberRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddBoardMember(
        @NotNull( message = "User id cannot be null")
        UUID userId,
        @NotNull( message = "Role cannot be null")
        BoardMemberRole role
) {
}
