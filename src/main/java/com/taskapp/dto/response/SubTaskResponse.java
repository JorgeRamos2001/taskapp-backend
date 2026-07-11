package com.taskapp.dto.response;

import java.util.UUID;

public record SubTaskResponse(
        UUID id,
        UUID boardTaskId,
        String title,
        Boolean completed
) {
}
