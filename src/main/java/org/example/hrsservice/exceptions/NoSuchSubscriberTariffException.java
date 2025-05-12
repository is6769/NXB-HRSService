package org.example.hrsservice.exceptions;

/**
 * Исключение, выбрасываемое, когда для абонента не найден соответствующий тариф.
 * Это может указывать на несоответствие данных или на отсутствие активного тарифа у абонента.
 */
public class NoSuchSubscriberTariffException extends RuntimeException {
    public NoSuchSubscriberTariffException(String message) {
        super(message);
    }
}
