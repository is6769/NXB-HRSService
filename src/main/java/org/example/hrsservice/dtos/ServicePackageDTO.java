package org.example.hrsservice.dtos;

import org.example.hrsservice.entities.ServicePackage;
import org.example.hrsservice.entities.ServiceType;

import java.util.List;

/**
 * DTO для представления информации о пакете услуг.
 *
 * @param id Уникальный идентификатор пакета услуг.
 * @param name Название пакета услуг.
 * @param description Описание пакета услуг.
 * @param serviceType Тип услуги, предоставляемой пакетом ({@link ServiceType}).
 * @param packageRules Список DTO правил ({@link PackageRuleDTO}), связанных с этим пакетом.
 */
public record ServicePackageDTO(
        Long id,
        String name,
        String description,
        ServiceType serviceType,
        List<PackageRuleDTO> packageRules
) {
    /**
     * Статический фабричный метод для создания {@link ServicePackageDTO} из сущности {@link ServicePackage}.
     * @param servicePackage Сущность пакета услуг.
     * @return DTO, представляющий пакет услуг.
     */
    public static ServicePackageDTO fromEntity(ServicePackage servicePackage) {
        return new ServicePackageDTO(
                servicePackage.getId(),
                servicePackage.getName(),
                servicePackage.getDescription(),
                servicePackage.getServiceType(),
                servicePackage.getPackageRules()
                        .stream()
                        .map(PackageRuleDTO::fromEntity)
                        .toList()
        );
    }
}
