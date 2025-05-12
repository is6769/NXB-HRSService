package org.example.hrsservice.dtos;

import org.example.hrsservice.entities.SubscriberTariff;

import java.time.LocalDateTime;

/**
 * DTO для представления информации о тарифе абонента.
 *
 * @param id Уникальный идентификатор записи тарифа абонента.
 * @param subscriberId Идентификатор абонента.
 * @param cycleStart Дата и время начала текущего цикла тарифа.
 * @param cycleEnd Дата и время окончания текущего цикла тарифа.
 * @param tariff DTO с информацией о самом тарифе ({@link TariffDTO}).
 */
public record SubscriberTariffDTO(
        Long id,
        Long subscriberId,
        LocalDateTime cycleStart,
        LocalDateTime cycleEnd,
        TariffDTO tariff
) {

    /**
     * Статический фабричный метод для создания {@link SubscriberTariffDTO} из сущности {@link SubscriberTariff}.
     * @param subscriberTariff Сущность тарифа абонента.
     * @return DTO, представляющий тариф абонента.
     */
    public static SubscriberTariffDTO fromEntity(SubscriberTariff subscriberTariff) {
        return new SubscriberTariffDTO(
                subscriberTariff.getId(),
                subscriberTariff.getSubscriberId(),
                subscriberTariff.getCycleStart(),
                subscriberTariff.getCycleEnd(),
                TariffDTO.fromEntity(subscriberTariff.getTariff())
        );
    }
}
