package org.example.hrsservice.entities;

import jakarta.persistence.*;
import lombok.*;

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

    private Integer usedAmount;

    private Integer limitAmount;

    private String unit;


}
