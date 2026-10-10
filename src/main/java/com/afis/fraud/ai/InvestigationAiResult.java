package com.afis.fraud.ai;

import java.util.List;

public record InvestigationAiResult(
        String summary,
        List<String> findings,
        List<String> suspiciousIndicators,
        List<String> normalIndicators,
        String recommendation
) {
}