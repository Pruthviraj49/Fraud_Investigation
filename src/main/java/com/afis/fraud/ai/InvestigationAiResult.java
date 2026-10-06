package com.afis.fraud.ai;

public record InvestigationAiResult(
        String summary,
        String findings,
        String recommendation
) {
}