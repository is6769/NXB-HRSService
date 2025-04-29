package org.example.hrsservice.exceptions;

public class NoSuchTariffPresent extends RuntimeException {
    public NoSuchTariffPresent(String message) {
        super(message);
    }
}
