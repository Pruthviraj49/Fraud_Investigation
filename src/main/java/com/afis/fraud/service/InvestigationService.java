package com.afis.fraud.service;

import com.afis.fraud.ai.FraudAssistant;
import com.afis.fraud.ai.InvestigationAiResult;
import com.afis.fraud.entity.Evidence;
import com.afis.fraud.entity.EvidenceType;
import com.afis.fraud.entity.Investigation;
import com.afis.fraud.entity.InvestigationStatus;
import com.afis.fraud.risk.FraudRiskEngine;
import com.afis.fraud.risk.RiskAssessment;
import com.afis.fraud.entity.Transaction;
import com.afis.fraud.repository.EvidenceRepository;
import com.afis.fraud.repository.InvestigationRepository;
import com.afis.fraud.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvestigationService {

        private final TransactionRepository transactionRepository;
        private final InvestigationRepository investigationRepository;
        private final FraudAssistant fraudAssistant;
        private final FraudRiskEngine fraudRiskEngine;
        private final EvidenceRepository evidenceRepository;

        public InvestigationService(
                        TransactionRepository transactionRepository,
                        InvestigationRepository investigationRepository,
                        FraudAssistant fraudAssistant,
                        FraudRiskEngine fraudRiskEngine,
                        EvidenceRepository evidenceRepository) {

                this.transactionRepository = transactionRepository;
                this.investigationRepository = investigationRepository;
                this.fraudAssistant = fraudAssistant;
                this.fraudRiskEngine = fraudRiskEngine;
                this.evidenceRepository = evidenceRepository;
        }

        @Transactional
        public void investigate(String transactionReference) {

                Transaction transaction = transactionRepository
                                .findByTransactionReference(transactionReference)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Transaction not found: "
                                                                + transactionReference));

                if (investigationRepository
                                .existsByTransactionId(transaction.getId())) {

                        return;
                }

                InvestigationAiResult aiResult = fraudAssistant.investigate(
                                "Investigate transaction "
                                                + transactionReference);

                RiskAssessment riskAssessment = fraudRiskEngine.evaluate(transaction);
                InvestigationStatus status = riskAssessment.totalScore() > 50
                                ? InvestigationStatus.PENDING
                                : InvestigationStatus.COMPLETED;
                Investigation investigation = Investigation.create(
                                transaction,
                                status,
                                riskAssessment.totalScore(),
                                riskAssessment.riskLevel(),
                                aiResult.summary());
                Investigation savedInvestigation = investigationRepository.save(investigation);
                saveEvidence(savedInvestigation, aiResult, riskAssessment);

        }

        private void saveEvidence(
                        Investigation investigation,
                        InvestigationAiResult aiResult,
                        RiskAssessment riskAssessment) {

                List<Evidence> evidence = new ArrayList<>();

                riskAssessment.triggeredRules()
                                .forEach(rule -> evidence.add(
                                                Evidence.create(
                                                                investigation,
                                                                EvidenceType.DETERMINISTIC_RULE,
                                                                "%s: %s".formatted(
                                                                                rule.ruleCode(),
                                                                                rule.description()))));

                aiResult.findings()
                                .forEach(value -> evidence.add(
                                                Evidence.create(
                                                                investigation,
                                                                EvidenceType.FINDING,
                                                                value)));

                aiResult.suspiciousIndicators()
                                .forEach(value -> evidence.add(
                                                Evidence.create(
                                                                investigation,
                                                                EvidenceType.SUSPICIOUS_INDICATOR,
                                                                value)));

                aiResult.normalIndicators()
                                .forEach(value -> evidence.add(
                                                Evidence.create(
                                                                investigation,
                                                                EvidenceType.NORMAL_INDICATOR,
                                                                value)));

                if (aiResult.recommendation() != null &&
                                !aiResult.recommendation().isBlank()) {

                        evidence.add(
                                        Evidence.create(
                                                        investigation,
                                                        EvidenceType.RECOMMENDATION,
                                                        aiResult.recommendation()));
                }

                evidenceRepository.saveAll(evidence);
        }
}