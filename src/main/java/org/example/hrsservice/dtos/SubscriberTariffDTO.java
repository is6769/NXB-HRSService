package org.example.hrsservice.dtos;

import org.example.hrsservice.entities.SubscriberTariff;

import java.time.LocalDateTime;

public record SubscriberTariffDTO(
        Long id,
        Long subscriberId,
        LocalDateTime cycleStart,
        LocalDateTime cycleEnd,
        TariffDTO tariff
) {

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
