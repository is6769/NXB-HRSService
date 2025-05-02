package org.example.hrsservice.dtos;

import org.example.hrsservice.entities.ConditionNode;
import org.example.hrsservice.entities.PackageRule;
import org.example.hrsservice.entities.RuleType;

import java.math.BigDecimal;

public record PackageRuleDTO(
        Long id,
        RuleType ruleType,
        BigDecimal value,
        String unit,
        ConditionNode condition
) {

    public static PackageRuleDTO fromEntity(PackageRule packageRule){
        return new PackageRuleDTO(
                packageRule.getId(),
                packageRule.getRuleType(),
                packageRule.getValue(),
                packageRule.getUnit(),
                packageRule.getCondition()
        );
    }
}
