package org.example.hrsservice.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tariffs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"subscriberTariffs","tariffPackages"})
public class Tariff{

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
    @JsonBackReference
    private List<SubscriberTariff> subscriberTariffs;

    @OneToMany(mappedBy = "tariff")
    @JsonBackReference
    private List<TariffPackage> tariffPackages;
}
