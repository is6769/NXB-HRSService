package org.example.hrsservice.exceptions;

/**
 * Исключение, выбрасываемое при использовании неподдерживаемого оператора
 * в условиях правил тарификации.
 */
public class UnsupportedOperatorException extends RuntimeException {
    public UnsupportedOperatorException(String message) {
        super(message);
    }
}
