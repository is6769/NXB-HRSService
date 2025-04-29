package org.example.hrsservice.exceptions;

public class CannotChargeCallException extends RuntimeException {
    public CannotChargeCallException(String message) {
        super(message);
    }
}
