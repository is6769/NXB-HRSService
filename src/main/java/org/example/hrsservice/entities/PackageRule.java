package org.example.hrsservice.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "package_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageRule extends CreatedUpdateAtSuperClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_package_id")
    private ServicePackage servicePackage;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false)
    private RuleType ruleType;

    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Column(name = "unit", nullable = false)
    private String unit;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "period_type", nullable = false)
//    private PeriodType periodType;

    @Column(name = "condition", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String condition;

}
