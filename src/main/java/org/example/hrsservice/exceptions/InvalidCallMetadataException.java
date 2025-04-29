package org.example.hrsservice.exceptions;

public class InvalidCallMetadataException extends RuntimeException {
    public InvalidCallMetadataException(String field) {
        super("The field: %s is not present in metadata.".formatted(field));
    }
}
