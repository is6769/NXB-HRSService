package org.example.hrsservice.services;

import org.example.hrsservice.dtos.UsageWithMetadataDTO;
import org.example.hrsservice.entities.ConditionNode;
import org.example.hrsservice.entities.PackageRule;
import org.example.hrsservice.entities.RuleType;
import org.example.hrsservice.exceptions.UnsupportedConditionTypeException;
import org.example.hrsservice.exceptions.UnsupportedOperatorException;

import java.util.ArrayList;
import java.util.List;


public class RuleFinderService {

    public PackageRule findRuleThatMatchesConditionAndType(List<PackageRule> rules, UsageWithMetadataDTO usageWithMetadataDTO, RuleType ruleType) {
        for (PackageRule rule: rules){
            if (rule.getRuleType().equals(ruleType)){
                if (cdrMatchesCondition(usageWithMetadataDTO,rule.getCondition())){
                    return rule;
                }
            }
        }
        //this is valid answer cause we can have 0 active cost rules,and 0 active limit rules
        return null;
    }

    private boolean cdrMatchesCondition(UsageWithMetadataDTO usageWithMetadataDTO, ConditionNode condition) {
        String conditionType = condition.getType();
        if ("always_true".equals(conditionType)) return true;
        else if ("field".equals(conditionType)) return ifCdrMatchesFieldCondition(usageWithMetadataDTO,condition);
        else if ("and".equals(conditionType)) {
            List<Boolean> results = new ArrayList<>();
            condition.getConditions().forEach(subCondition -> results.add(cdrMatchesCondition(usageWithMetadataDTO,subCondition)));
            return !results.contains(false);
        } else if ("or".equals(conditionType)) {
            List<Boolean> results = new ArrayList<>();
            condition.getConditions().forEach(subCondition -> results.add(cdrMatchesCondition(usageWithMetadataDTO,subCondition)));
            return results.contains(true);
        }

        throw new UnsupportedConditionTypeException("Condition type: %s is unsupported.".formatted(conditionType));
    }

    private boolean ifCdrMatchesFieldCondition(UsageWithMetadataDTO usageWithMetadataDTO, ConditionNode condition) {
        String fieldName = condition.getField();
        String operator = condition.getOperator();
        String conditionValue = condition.getValue();
        if (usageWithMetadataDTO.metadata().has(fieldName)){
            return compareMetadataValueWithConditionValueViaOperator(usageWithMetadataDTO.metadata().get(fieldName).asText(), operator, conditionValue);
        }
        return false;
    }

    private boolean compareMetadataValueWithConditionValueViaOperator(String metadataValue, String operator, String conditionValue) {
        if ("equals".equals(operator)) return metadataValue.equals(conditionValue);
        else if ("not_equals".equals(operator)) return !metadataValue.equals(conditionValue);
        throw new UnsupportedOperatorException("Operator: %s is unsupported.".formatted(operator));
    }
}
