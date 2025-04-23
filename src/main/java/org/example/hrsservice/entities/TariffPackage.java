package org.example.hrsservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tariff_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffPackage extends CreatedUpdateAtSuperClass{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "service_package_id")
    private ServicePackage servicePackage;

    private Integer priority;


}
