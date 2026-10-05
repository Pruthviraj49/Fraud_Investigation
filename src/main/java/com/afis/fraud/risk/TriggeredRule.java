package com.afis.fraud.risk;

public record TriggeredRule(
        String ruleCode,
        String description,
        int score
) {
}