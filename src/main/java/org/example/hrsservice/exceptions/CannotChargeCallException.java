package org.example.hrsservice.exceptions;

/**
 * Исключение, выбрасываемое в случае невозможности списания средств за звонок.
 */
public class CannotChargeCallException extends RuntimeException {
    public CannotChargeCallException(String message) {
        super(message);
    }
}
