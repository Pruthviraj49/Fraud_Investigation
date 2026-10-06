package com.afis.fraud.risk;

import com.afis.fraud.entity.RiskLevel;

import java.util.List;

public record RiskAssessment(
        List<TriggeredRule> triggeredRules,
        int totalScore,
        RiskLevel riskLevel
) {
}