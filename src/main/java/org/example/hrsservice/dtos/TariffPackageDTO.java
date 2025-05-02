package org.example.hrsservice.dtos;

import org.example.hrsservice.entities.ServicePackage;
import org.example.hrsservice.entities.TariffPackage;

public record TariffPackageDTO(
        Long id,
        Integer priority,
        ServicePackageDTO servicePackage
) {
    public static TariffPackageDTO fromEntity(TariffPackage tariffPackage){
        return new TariffPackageDTO(
                tariffPackage.getId(),
                tariffPackage.getPriority(),
                ServicePackageDTO.fromEntity(tariffPackage.getServicePackage())
        );
    }
}
