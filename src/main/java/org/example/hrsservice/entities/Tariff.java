package org.example.hrsservice.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tariffs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff extends CreatedUpdateAtSuperClass{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    
    @Column(name = "cycle_size", nullable = false)
    private String cycleSize;

    @Column(name = "is_active", nullable = false)
    private Boolean is_active;

    @OneToMany(mappedBy = "tariff")
    private List<SubscriberTariff> subscriberTariffs;

    @OneToMany(mappedBy = "tariff")
    private List<TariffPackage> tariffPackages;
}
