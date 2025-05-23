package org.example.hrsservice.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;


@Entity
@Table(name = "package_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageRule{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
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

    @Column(name = "condition", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private ConditionNode condition;
}
