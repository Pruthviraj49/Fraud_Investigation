package com.afis.fraud.service;
import com.afis.fraud.ai.FraudAssistant;
import com.afis.fraud.ai.InvestigationAiResult;
import com.afis.fraud.entity.Investigation;
import com.afis.fraud.entity.InvestigationStatus;
import com.afis.fraud.risk.FraudRiskEngine;
import com.afis.fraud.risk.RiskAssessment;
import com.afis.fraud.entity.Transaction;
import com.afis.fraud.repository.InvestigationRepository;
import com.afis.fraud.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvestigationService {

    private final TransactionRepository transactionRepository;
    private final InvestigationRepository investigationRepository;
    private final FraudAssistant fraudAssistant;
    private final FraudRiskEngine fraudRiskEngine;

    public InvestigationService(
            TransactionRepository transactionRepository,
            InvestigationRepository investigationRepository,
            FraudAssistant fraudAssistant,
            FraudRiskEngine fraudRiskEngine) {

        this.transactionRepository = transactionRepository;
        this.investigationRepository = investigationRepository;
        this.fraudAssistant = fraudAssistant;
        this.fraudRiskEngine = fraudRiskEngine;
    }

    @Transactional
    public void investigate(String transactionReference) {

        Transaction transaction = transactionRepository
                .findByTransactionReference(transactionReference)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Transaction not found: "
                                        + transactionReference
                        )
                );

        if (investigationRepository
                .existsByTransactionId(transaction.getId())) {

            return;
        }

        InvestigationAiResult aiResult =
                fraudAssistant.investigate(
                        "Investigate transaction "
                                + transactionReference
                );

        RiskAssessment riskAssessment =
                fraudRiskEngine.evaluate(transaction);
InvestigationStatus status = riskAssessment.totalScore() > 50
                ? InvestigationStatus.PENDING
                : InvestigationStatus.COMPLETED;
        Investigation investigation =
                Investigation.create(
                        transaction,
                        status,
                        riskAssessment.totalScore(),
                        riskAssessment.riskLevel(),
                        aiResult.summary()
                );

        investigationRepository.save(investigation);
    }
}