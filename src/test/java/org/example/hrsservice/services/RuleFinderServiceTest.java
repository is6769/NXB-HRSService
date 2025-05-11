package org.example.hrsservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.hrsservice.dtos.UsageWithMetadataDTO;
import org.example.hrsservice.entities.ConditionNode;
import org.example.hrsservice.entities.PackageRule;
import org.example.hrsservice.entities.RuleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleFinderServiceTest {

    private RuleFinderService ruleFinderService;
    private UsageWithMetadataDTO usageDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ruleFinderService = new RuleFinderService();
        
        ObjectNode metadataNode = objectMapper.createObjectNode();
        metadataNode.put("callId", 1L);
        metadataNode.put("callType", "01");
        metadataNode.put("servicedMsisdn", "79001234567");
        metadataNode.put("otherMsisdn", "79007654321");
        metadataNode.put("startDateTime", LocalDateTime.of(2024, 1, 1, 10, 0, 0).toString());
        metadataNode.put("finishDateTime", LocalDateTime.of(2024, 1, 1, 10, 5, 0).toString());
        metadataNode.put("durationInMinutes", 5);
        metadataNode.put("otherOperator", "Ромашка");
        
        usageDTO = new UsageWithMetadataDTO(1L, metadataNode);
    }
    
    private ConditionNode createFieldCondition(String field, String operator, String value) {
        ConditionNode node = new ConditionNode();
        node.setType("field");
        node.setField(field);
        node.setOperator(operator);
        node.setValue(value);
        return node;
    }

    private ConditionNode createAlwaysTrueCondition() {
        ConditionNode node = new ConditionNode();
        node.setType("always_true");
        return node;
    }

    private ConditionNode createLogicalCondition(String type, List<ConditionNode> conditions) {
        ConditionNode node = new ConditionNode();
        node.setType(type);
        node.setConditions(conditions);
        return node;
    }

    private PackageRule createRule(Long id, RuleType type, ConditionNode condition) {
        PackageRule rule = new PackageRule();
        rule.setId(id);
        rule.setRuleType(type);
        rule.setCondition(condition);
        
        
        if (type == RuleType.RATE) {
            rule.setValue(BigDecimal.TEN); 
        } else if (type == RuleType.LIMIT) {
            rule.setValue(BigDecimal.valueOf(100)); 
            rule.setUnit("minutes");
        } else if (type == RuleType.COST) {
            rule.setValue(BigDecimal.valueOf(50)); 
        }
        
        return rule;
    }

    
    @Test
    void findRule_fieldConditionEquals_matches() {
        ConditionNode condition = createFieldCondition("callType", "equals", "01");
        PackageRule rule = createRule(1L, RuleType.RATE, condition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNotNull(result);
        assertEquals(rule.getId(), result.getId());
    }

    @Test
    void findRule_fieldConditionEquals_doesNotMatch() {
        ConditionNode condition = createFieldCondition("callType", "equals", "02");
        PackageRule rule = createRule(1L, RuleType.RATE, condition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNull(result);
    }

    @Test
    void findRule_fieldConditionNotEquals_matches() {
        ConditionNode condition = createFieldCondition("callType", "not_equals", "02");
        PackageRule rule = createRule(1L, RuleType.RATE, condition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNotNull(result);
        assertEquals(rule.getId(), result.getId());
    }

    @Test
    void findRule_fieldConditionNotEquals_doesNotMatch() {
        ConditionNode condition = createFieldCondition("callType", "not_equals", "01");
        PackageRule rule = createRule(1L, RuleType.RATE, condition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNull(result);
    }

    @Test
    void findRule_fieldDoesNotExist_doesNotMatch() {
        ConditionNode condition = createFieldCondition("nonExistentField", "equals", "value");
        PackageRule rule = createRule(1L, RuleType.RATE, condition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNull(result);
    }

    
    @Test
    void findRule_alwaysTrueCondition_matches() {
        ConditionNode condition = createAlwaysTrueCondition();
        PackageRule rule = createRule(1L, RuleType.RATE, condition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNotNull(result);
        assertEquals(rule.getId(), result.getId());
    }

    
    @Test
    void findRule_andCondition_allMatch_returnsRule() {
        ConditionNode condition1 = createFieldCondition("callType", "equals", "01");
        ConditionNode condition2 = createFieldCondition("otherOperator", "equals", "Ромашка");
        ConditionNode andCondition = createLogicalCondition("and", List.of(condition1, condition2));
        
        PackageRule rule = createRule(1L, RuleType.RATE, andCondition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNotNull(result);
        assertEquals(rule.getId(), result.getId());
    }

    @Test
    void findRule_andCondition_oneDoesNotMatch_returnsNull() {
        ConditionNode condition1 = createFieldCondition("callType", "equals", "01");
        ConditionNode condition2 = createFieldCondition("otherOperator", "equals", "NotRomashka");
        ConditionNode andCondition = createLogicalCondition("and", List.of(condition1, condition2));
        
        PackageRule rule = createRule(1L, RuleType.RATE, andCondition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNull(result);
    }

    @Test
    void findRule_andCondition_noneMatch_returnsNull() {
        ConditionNode condition1 = createFieldCondition("callType", "equals", "02");
        ConditionNode condition2 = createFieldCondition("otherOperator", "equals", "NotRomashka");
        ConditionNode andCondition = createLogicalCondition("and", List.of(condition1, condition2));
        
        PackageRule rule = createRule(1L, RuleType.RATE, andCondition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNull(result);
    }

    
    @Test
    void findRule_orCondition_allMatch_returnsRule() {
        ConditionNode condition1 = createFieldCondition("callType", "equals", "01");
        ConditionNode condition2 = createFieldCondition("otherOperator", "equals", "Ромашка");
        ConditionNode orCondition = createLogicalCondition("or", List.of(condition1, condition2));
        
        PackageRule rule = createRule(1L, RuleType.RATE, orCondition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNotNull(result);
        assertEquals(rule.getId(), result.getId());
    }

    @Test
    void findRule_orCondition_oneMatches_returnsRule() {
        ConditionNode condition1 = createFieldCondition("callType", "equals", "02"); // Does not match
        ConditionNode condition2 = createFieldCondition("otherOperator", "equals", "Ромашка"); // Matches
        ConditionNode orCondition = createLogicalCondition("or", List.of(condition1, condition2));
        
        PackageRule rule = createRule(1L, RuleType.RATE, orCondition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNotNull(result);
        assertEquals(rule.getId(), result.getId());
    }

    @Test
    void findRule_orCondition_noneMatch_returnsNull() {
        ConditionNode condition1 = createFieldCondition("callType", "equals", "02"); // Does not match
        ConditionNode condition2 = createFieldCondition("otherOperator", "equals", "NotRomashka"); // Does not match
        ConditionNode orCondition = createLogicalCondition("or", List.of(condition1, condition2));
        
        PackageRule rule = createRule(1L, RuleType.RATE, orCondition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNull(result);
    }

    
    @Test
    void findRule_nestedConditions_matches() {
        ConditionNode orCondition = createLogicalCondition("or", List.of(
            createFieldCondition("callType", "equals", "01"),
            createFieldCondition("otherOperator", "equals", "NotRomashka")
        ));
        ConditionNode andCondition = createLogicalCondition("and", List.of(
            orCondition,
            createFieldCondition("durationInMinutes", "equals", "5")
        ));
        
        PackageRule rule = createRule(1L, RuleType.RATE, andCondition);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        
        assertNotNull(result);
        assertEquals(rule.getId(), result.getId());
    }

    
    @Test
    void findRule_ruleTypeMismatch_returnsNull() {
        ConditionNode condition = createAlwaysTrueCondition();
        PackageRule rule = createRule(1L, RuleType.COST, condition); 
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(List.of(rule), usageDTO, RuleType.RATE);
        
        assertNull(result);
    }
    
    @Test
    void findRule_multipleRules_returnsFirstMatch() {
        PackageRule ruleRate = createRule(1L, RuleType.RATE, createFieldCondition("callType", "equals", "01"));
        PackageRule ruleCost = createRule(2L, RuleType.COST, createAlwaysTrueCondition());
        PackageRule ruleLimit = createRule(3L, RuleType.LIMIT, createAlwaysTrueCondition());
        
        List<PackageRule> rules = List.of(ruleCost, ruleLimit, ruleRate);
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(rules, usageDTO, RuleType.RATE);
        
        assertNotNull(result);
        assertEquals(ruleRate.getId(), result.getId());
        assertEquals(RuleType.RATE, result.getRuleType());
    }
    
    @Test
    void findRule_emptyRuleList_returnsNull() {
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(
                Collections.emptyList(), usageDTO, RuleType.RATE);
        
        assertNull(result);
    }

    @Test
    void findRule_noRulesOfRequestedType_returnsNull() {
        PackageRule ruleCost = createRule(1L, RuleType.COST, createAlwaysTrueCondition());
        PackageRule ruleLimit = createRule(2L, RuleType.LIMIT, createAlwaysTrueCondition());
        
        List<PackageRule> rules = List.of(ruleCost, ruleLimit);
        
        
        PackageRule result = ruleFinderService.findRuleThatMatchesConditionAndType(rules, usageDTO, RuleType.RATE);
        
        
        assertNull(result);
    }
}
