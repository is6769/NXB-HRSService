package org.example.hrsservice.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePackage extends CreatedUpdateAtSuperClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;


    @OneToMany(mappedBy = "servicePackage")
    private List<PackageRule> packageRules;

    @OneToMany(mappedBy = "servicePackage")
    private List<SubscriberPackageUsage> subscriberPackageUsages;

    @OneToMany(mappedBy = "servicePackage")
    private List<TariffPackage> tariffPackages;
}
