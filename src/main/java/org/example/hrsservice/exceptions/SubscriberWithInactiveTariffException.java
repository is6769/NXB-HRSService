package org.example.hrsservice.exceptions;

public class SubscriberWithInactiveTariffException extends RuntimeException {
    public SubscriberWithInactiveTariffException(String message) {
        super(message);
    }
}
