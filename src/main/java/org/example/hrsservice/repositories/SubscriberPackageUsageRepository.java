package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.SubscriberPackageUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link SubscriberPackageUsage}.
 * Предоставляет методы для поиска информации об использовании пакетов услуг абонентами.
 */
public interface SubscriberPackageUsageRepository extends JpaRepository<SubscriberPackageUsage, Long> {

    /**
     * Находит все активные (не удаленные) записи об использовании пакетов для указанного ID абонента.
     * @param subscriberId ID абонента.
     * @return Список активных использований пакетов.
     */
    List<SubscriberPackageUsage> findAllBySubscriberIdAndIsDeletedFalse(Long subscriberId);

    /**
     * Находит активную (не удаленную) запись об использовании конкретного пакета услуг указанным абонентом.
     * @param servicePackageId ID пакета услуг.
     * @param subscriberId ID абонента.
     * @return Запись об использовании пакета или {@code null}, если не найдена.
     */
    SubscriberPackageUsage findByServicePackageIdAndIsDeletedFalseAndSubscriberId(Long servicePackageId, Long subscriberId);
}
