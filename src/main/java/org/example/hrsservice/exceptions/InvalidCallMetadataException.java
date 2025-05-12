package org.example.hrsservice.exceptions;

/**
 * Исключение, выбрасываемое при обнаружении некорректных или отсутствующих
 * метаданных звонка, необходимых для тарификации.
 */
public class InvalidCallMetadataException extends RuntimeException {
    public InvalidCallMetadataException(String message) {
        super(message);
    }
}
