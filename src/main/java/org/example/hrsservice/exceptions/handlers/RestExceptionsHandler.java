package org.example.hrsservice.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.hrsservice.dtos.ExceptionDTO;
import org.example.hrsservice.exceptions.*;
import org.example.hrsservice.services.SystemDatetimeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionsHandler {

    private final SystemDatetimeService systemDatetimeService;

    public RestExceptionsHandler(SystemDatetimeService systemDatetimeService) {
        this.systemDatetimeService = systemDatetimeService;
    }

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
