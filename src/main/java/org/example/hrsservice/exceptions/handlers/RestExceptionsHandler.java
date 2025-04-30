package org.example.hrsservice.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.hrsservice.dtos.ExceptionDTO;
import org.example.hrsservice.exceptions.CannotChargeCallException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionsHandler {

    @ExceptionHandler(exception = CannotChargeCallException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleCannotChargeCallException(HttpServletRequest request, Exception ex){
        return new ExceptionDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getRequestURL().toString()
        );
    }
}
