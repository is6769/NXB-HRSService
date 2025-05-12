package org.example.hrsservice.services;

import org.example.hrsservice.dtos.UsageWithMetadataDTO;
import org.example.hrsservice.entities.ConditionNode;
import org.example.hrsservice.entities.PackageRule;
import org.example.hrsservice.entities.RuleType;
import org.example.hrsservice.exceptions.UnsupportedConditionTypeException;
import org.example.hrsservice.exceptions.UnsupportedOperatorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для поиска правил тарификации, соответствующих заданным условиям и типу.
 * Используется для определения применимых правил (LIMIT, RATE, COST) на основе метаданных звонка.
 */
public class RuleFinderService {

    /**
     * Находит первое правило из списка, которое соответствует указанному типу и условиям из метаданных звонка.
     * @param rules Список правил для поиска.
     * @param usageWithMetadataDTO DTO с метаданными звонка.
     * @param ruleType Требуемый тип правила (LIMIT, RATE, COST).
     * @return Найденное правило {@link PackageRule} или {@code null}, если подходящее правило не найдено.
     */
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

    /**
     * Проверяет, соответствует ли CDR (Call Detail Record), представленный в {@link UsageWithMetadataDTO}, заданному условию.
     * Рекурсивно обрабатывает вложенные условия.
     * @param usageWithMetadataDTO DTO с метаданными звонка.
     * @param condition Узел условия {@link ConditionNode}.
     * @return {@code true}, если CDR соответствует условию, иначе {@code false}.
     * @throws UnsupportedConditionTypeException если тип условия не поддерживается.
     */
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

    /**
     * Проверяет, соответствует ли CDR условию типа "field".
     * Сравнивает значение указанного поля из метаданных CDR с заданным значением условия, используя указанный оператор.
     * @param usageWithMetadataDTO DTO с метаданными звонка.
     * @param condition Узел условия типа "field".
     * @return {@code true}, если условие выполняется, иначе {@code false}.
     */
    private boolean ifCdrMatchesFieldCondition(UsageWithMetadataDTO usageWithMetadataDTO, ConditionNode condition) {
        String fieldName = condition.getField();
        String operator = condition.getOperator();
        String conditionValue = condition.getValue();
        if (usageWithMetadataDTO.metadata().has(fieldName)){
            return compareMetadataValueWithConditionValueViaOperator(usageWithMetadataDTO.metadata().get(fieldName).asText(), operator, conditionValue);
        }
        return false;
    }

    /**
     * Сравнивает значение из метаданных с значением условия, используя указанный оператор.
     * @param metadataValue Значение из метаданных.
     * @param operator Оператор сравнения ("equals", "not_equals").
     * @param conditionValue Значение из условия.
     * @return {@code true}, если сравнение истинно, иначе {@code false}.
     * @throws UnsupportedOperatorException если оператор не поддерживается.
     */
    private boolean compareMetadataValueWithConditionValueViaOperator(String metadataValue, String operator, String conditionValue) {
        if ("equals".equals(operator)) return metadataValue.equals(conditionValue);
        else if ("not_equals".equals(operator)) return !metadataValue.equals(conditionValue);
        throw new UnsupportedOperatorException("Operator: %s is unsupported.".formatted(operator));
    }
}
