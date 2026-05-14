package com.ssafy.home.common;

import java.time.LocalDateTime;

public record ErrorResponse(
        boolean success,
        String message,
        String code,
        int status,
        String path,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(String message, String code, int status, String path) {
        return new ErrorResponse(false, message, code, status, path, LocalDateTime.now());
    }
}
