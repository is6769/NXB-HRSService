package org.example.hrsservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "subscriber_package_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriberPackageUsage extends CreatedUpdateAtSuperClass{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscriber_id", nullable = false)
    private Long subscriberId;

    @ManyToOne
    @JoinColumn(name = "service_package_id")
    private ServicePackage servicePackage;

    private BigDecimal usedAmount;

    private BigDecimal limitAmount;

    private String unit;

    private Boolean isDeleted;


}
