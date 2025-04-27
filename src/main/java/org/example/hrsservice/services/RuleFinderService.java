package org.example.hrsservice.services;

import org.example.hrsservice.dtos.requests.UsageWithMetadataDTO;
import org.example.hrsservice.entities.ConditionNode;
import org.example.hrsservice.entities.PackageRule;
import org.example.hrsservice.entities.RuleType;
import org.springframework.stereotype.Service;

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
        //TODO throw error
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

        return false;
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
        if ("not_equals".equals(operator)) return !metadataValue.equals(conditionValue);
        return true;
    }
}
