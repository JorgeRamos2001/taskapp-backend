package com.taskapp.user.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String urlAvatar
) {
}
