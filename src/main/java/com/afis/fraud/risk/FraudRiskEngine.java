package com.afis.fraud.risk;

import com.afis.fraud.entity.Transaction;
import com.afis.fraud.entity.RiskLevel;
import com.afis.fraud.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudRiskEngine {

    private static final BigDecimal HIGH_AMOUNT_THRESHOLD =
            new BigDecimal("50000");

    private final TransactionRepository transactionRepository;

    public FraudRiskEngine(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public RiskAssessment evaluate(Transaction transaction) {

        List<TriggeredRule> triggeredRules = new ArrayList<>();

        checkHighAmount(transaction, triggeredRules);
        checkNewDevice(transaction, triggeredRules);

        int totalScore = triggeredRules.stream()
                .mapToInt(TriggeredRule::score)
                .sum();

        RiskLevel riskLevel = determineRiskLevel(totalScore);

        return new RiskAssessment(
                List.copyOf(triggeredRules),
                totalScore,
                riskLevel
        );
    }

    private void checkHighAmount(
            Transaction transaction,
            List<TriggeredRule> triggeredRules) {

        if (transaction.getAmount()
                .compareTo(HIGH_AMOUNT_THRESHOLD) > 0) {

            triggeredRules.add(
                    new TriggeredRule(
                            "HIGH_AMOUNT",
                            "Transaction amount exceeds 50000",
                            25
                    )
            );
        }
    }

    private void checkNewDevice(
            Transaction transaction,
            List<TriggeredRule> triggeredRules) {

        boolean deviceSeenBefore =
                transactionRepository
                        .existsByCustomerIdAndDeviceIdAndIdNot(
                                transaction.getCustomer().getId(),
                                transaction.getDeviceId(),
                                transaction.getId()
                        );

        if (!deviceSeenBefore) {

            triggeredRules.add(
                    new TriggeredRule(
                            "NEW_DEVICE",
                            "Device has not been observed in previous customer transactions",
                            20
                    )
            );
        }
    }

    private RiskLevel determineRiskLevel(int score) {

        if (score >= 60) {
            return RiskLevel.HIGH;
        }

        if (score >= 30) {
            return RiskLevel.MEDIUM;
        }

        return RiskLevel.LOW;
    }
}