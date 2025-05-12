package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.SubscriberTariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления сущностями {@link SubscriberTariff}.
 * Предоставляет методы для поиска тарифов абонентов по различным критериям.
 */
public interface SubscriberTariffRepository extends JpaRepository<SubscriberTariff, Long> {

    /**
     * Находит тариф абонента по ID абонента.
     * @param subscriberId ID абонента.
     * @return {@link Optional} с тарифом абонента, если найден.
     */
    Optional<SubscriberTariff> findBySubscriberId(Long subscriberId);

    /**
     * Находит все тарифы абонентов, у которых дата окончания цикла раньше указанной
     * и размер цикла тарифа не равен указанному значению.
     * @param cycleEndBefore Дата для сравнения окончания цикла.
     * @param tariffCycleSize Размер цикла тарифа для исключения.
     * @return Список тарифов абонентов.
     */
    List<SubscriberTariff> findAllByCycleEndBeforeAndTariff_CycleSizeNot(LocalDateTime cycleEndBefore, String tariffCycleSize);
}
