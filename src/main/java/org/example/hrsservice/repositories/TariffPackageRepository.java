package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.ServiceType;
import org.example.hrsservice.entities.TariffPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link TariffPackage}.
 * Предоставляет методы для поиска пакетов тарифов по различным критериям.
 */
public interface TariffPackageRepository extends JpaRepository<TariffPackage,Long> {
    /**
     * Находит все пакеты тарифов по ID тарифа.
     * @param tariffId ID тарифа.
     * @return Список пакетов тарифов.
     */
    List<TariffPackage> findAllByTariff_Id(Long tariffId);

    /**
     * Находит все пакеты тарифов по ID тарифа и типу сервисного пакета.
     * @param tariffId ID тарифа.
     * @param servicePackageServiceType Тип сервисного пакета.
     * @return Список пакетов тарифов.
     */
    List<TariffPackage> findAllByTariff_IdAndServicePackageServiceType(Long tariffId, ServiceType servicePackageServiceType);
}
