package org.example.hrsservice.exceptions;

/**
 * Исключение, выбрасываемое при обнаружении неподдерживаемого типа условия.
 * Это может произойти, например, при обработке правил тарификации с неизвестным типом условия.
 */
public class UnsupportedConditionTypeException extends RuntimeException {
    public UnsupportedConditionTypeException(String message) {
        super(message);
    }
}
