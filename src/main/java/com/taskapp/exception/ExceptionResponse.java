package com.taskapp.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(
        Integer status,
        String message,
        Object details,
        String path,
        LocalDateTime timestamp
) {
}
