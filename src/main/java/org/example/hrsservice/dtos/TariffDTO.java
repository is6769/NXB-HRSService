package org.example.hrsservice.dtos;

import org.example.hrsservice.entities.Tariff;

import java.util.List;

public record TariffDTO(
        Long id,
        String name,
        String description,
        String cycleSize,
        Boolean is_active,
        List<TariffPackageDTO> tariffPackages
) {
    public static TariffDTO fromEntity(Tariff tariff) {
        return new TariffDTO(
                tariff.getId(),
                tariff.getName(),
                tariff.getDescription(),
                tariff.getCycleSize(),
                tariff.getIs_active(),
                tariff.getTariffPackages()
                        .stream()
                        .map(TariffPackageDTO::fromEntity)
                        .toList()
        );
    }
}
