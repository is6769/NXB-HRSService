package org.example.hrsservice.dtos;

import org.example.hrsservice.entities.ServicePackage;
import org.example.hrsservice.entities.ServiceType;

import java.util.List;

public record ServicePackageDTO(
        Long id,
        String name,
        String description,
        ServiceType serviceType,
        List<PackageRuleDTO> packageRules
        //List<SubscriberPackageUsage> subscriberPackageUsages
) {
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
