package org.example.hrsservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionNode {

    private String type;

    private String field;

    private String operator;

    private String value;

    private List<ConditionNode> conditions;
}
