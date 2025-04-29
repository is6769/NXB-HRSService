package org.example.hrsservice.exceptions;

public class NoSuchSubscriberTariffException extends RuntimeException {
    public NoSuchSubscriberTariffException(String message) {
        super(message);
    }
}
