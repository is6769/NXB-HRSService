package org.example.hrsservice.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "service_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"packageRules","subscriberPackageUsages", "tariffPackages"})
public class ServicePackage{
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


    @OneToMany(mappedBy = "servicePackage", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<PackageRule> packageRules;

    @OneToMany(mappedBy = "servicePackage", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<SubscriberPackageUsage> subscriberPackageUsages;

    @OneToMany(mappedBy = "servicePackage", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<TariffPackage> tariffPackages;
}
