package org.example.hrsservice.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.hrsservice.dtos.ExceptionDTO;
import org.example.hrsservice.exceptions.*;
import org.example.hrsservice.services.SystemDatetimeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений для REST контроллеров в HRSService.
 * Перехватывает специфичные для сервиса исключения и возвращает стандартизированные HTTP ответы
 * в формате {@link ExceptionDTO}.
 */
@RestControllerAdvice
public class RestExceptionsHandler {

    private final SystemDatetimeService systemDatetimeService;

    /**
     * Конструктор обработчика исключений.
     * @param systemDatetimeService Сервис для получения текущего системного времени.
     */
    public RestExceptionsHandler(SystemDatetimeService systemDatetimeService) {
        this.systemDatetimeService = systemDatetimeService;
    }

    /**
     * Обрабатывает исключение {@link CannotChargeCallException}.
     * Возвращает HTTP статус 404 (NOT_FOUND).
     * @param request HTTP запрос.
     * @param ex Перехваченное исключение.
     * @return {@link ExceptionDTO} с информацией об ошибке.
     */
    @ExceptionHandler(exception = CannotChargeCallException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleCannotChargeCallException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                systemDatetimeService.getSystemDatetime(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    /**
     * Обрабатывает исключение {@link InvalidCallMetadataException}.
     * Возвращает HTTP статус 400 (BAD_REQUEST).
     * @param request HTTP запрос.
     * @param ex Перехваченное исключение.
     * @return {@link ExceptionDTO} с информацией об ошибке.
     */
    @ExceptionHandler(exception = InvalidCallMetadataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleInvalidCallMetadataException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                systemDatetimeService.getSystemDatetime(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    /**
     * Обрабатывает исключение {@link NoSuchSubscriberTariffException}.
     * Возвращает HTTP статус 400 (BAD_REQUEST).
     * @param request HTTP запрос.
     * @param ex Перехваченное исключение.
     * @return {@link ExceptionDTO} с информацией об ошибке.
     */
    @ExceptionHandler(exception = NoSuchSubscriberTariffException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleNoSuchSubscriberTariffException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                systemDatetimeService.getSystemDatetime(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    /**
     * Обрабатывает исключение {@link NoSuchTariffException}.
     * Возвращает HTTP статус 404 (NOT_FOUND).
     * @param request HTTP запрос.
     * @param ex Перехваченное исключение.
     * @return {@link ExceptionDTO} с информацией об ошибке.
     */
    @ExceptionHandler(exception = NoSuchTariffException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleNoSuchTariffException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                systemDatetimeService.getSystemDatetime(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    /**
     * Обрабатывает исключение {@link SubscriberWithInactiveTariffException}.
     * Возвращает HTTP статус 400 (BAD_REQUEST).
     * @param request HTTP запрос.
     * @param ex Перехваченное исключение.
     * @return {@link ExceptionDTO} с информацией об ошибке.
     */
    @ExceptionHandler(exception = SubscriberWithInactiveTariffException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleSubscriberWithInactiveTariffException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                systemDatetimeService.getSystemDatetime(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    /**
     * Обрабатывает исключение {@link UnsupportedConditionTypeException}.
     * Возвращает HTTP статус 500 (INTERNAL_SERVER_ERROR).
     * @param request HTTP запрос.
     * @param ex Перехваченное исключение.
     * @return {@link ExceptionDTO} с информацией об ошибке.
     */
    @ExceptionHandler(exception = UnsupportedConditionTypeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDTO handleUnsupportedConditionTypeException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                systemDatetimeService.getSystemDatetime(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }

    /**
     * Обрабатывает исключение {@link UnsupportedOperatorException}.
     * Возвращает HTTP статус 500 (INTERNAL_SERVER_ERROR).
     * @param request HTTP запрос.
     * @param ex Перехваченное исключение.
     * @return {@link ExceptionDTO} с информацией об ошибке.
     */
    @ExceptionHandler(exception = UnsupportedOperatorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDTO handleUnsupportedOperatorException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                systemDatetimeService.getSystemDatetime(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }
}
