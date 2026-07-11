package com.taskapp.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RemoveBoardMember(
        @NotNull( message = "User id cannot be null")
        UUID userId
) {
}
