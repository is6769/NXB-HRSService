package org.example.hrsservice.dtos;

import java.time.LocalDateTime;

public record ExceptionDTO(
        LocalDateTime timestamp,
        Integer status,
        String exceptionType,
        String message,
        String url
) {
}
