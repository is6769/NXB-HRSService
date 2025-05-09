package org.example.hrsservice.exceptions;

public class NoSuchTariffException extends RuntimeException {
    public NoSuchTariffException(String message) {
        super(message);
    }
}
