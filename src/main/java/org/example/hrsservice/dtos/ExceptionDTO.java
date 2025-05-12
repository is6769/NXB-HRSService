package org.example.hrsservice.dtos;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для представления информации об исключении.
 * Используется для стандартизации формата ошибок, возвращаемых REST API.
 *
 * @param timestamp Временная метка возникновения исключения.
 * @param status HTTP статус код ошибки.
 * @param exceptionType Тип исключения (например, "NOT_FOUND", "BAD_REQUEST").
 * @param message Сообщение об ошибке.
 * @param url URL запроса, при обработке которого произошло исключение.
 */
public record ExceptionDTO(
        LocalDateTime timestamp,
        Integer status,
        String exceptionType,
        String message,
        String url
) {
}
