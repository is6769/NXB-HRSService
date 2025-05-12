package org.example.hrsservice.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Сущность, представляющая пакет услуг.
 * Пакет может содержать определенное количество минут, SMS, интернет-трафика и т.д.
 */
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

    /**
     * Название пакета услуг.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Описание пакета услуг.
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Тип услуги, предоставляемой пакетом (например, MINUTES, SMS).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;


    /**
     * Список правил, связанных с этим пакетом услуг.
     * Использует {@link JsonBackReference} для управления сериализацией JSON.
     */
    @OneToMany(mappedBy = "servicePackage", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<PackageRule> packageRules;

    /**
     * Список записей об использовании этого пакета абонентами.
     * Использует {@link JsonBackReference} для управления сериализацией JSON.
     */
    @OneToMany(mappedBy = "servicePackage", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<SubscriberPackageUsage> subscriberPackageUsages;

    /**
     * Список связей этого пакета с тарифами.
     * Использует {@link JsonBackReference} для управления сериализацией JSON.
     */
    @OneToMany(mappedBy = "servicePackage", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<TariffPackage> tariffPackages;
}
