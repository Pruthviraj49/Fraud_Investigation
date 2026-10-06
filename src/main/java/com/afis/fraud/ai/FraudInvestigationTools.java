package com.afis.fraud.ai;

import com.afis.fraud.entity.*;
import com.afis.fraud.repository.*;
import com.afis.fraud.risk.FraudRiskEngine;
import com.afis.fraud.risk.RiskAssessment;
import com.afis.fraud.risk.TriggeredRule;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class FraudInvestigationTools {

    private final TransactionRepository transactionRepository;
    private final FraudRiskEngine fraudRiskEngine;

    public FraudInvestigationTools(
            TransactionRepository transactionRepository,
            FraudRiskEngine fraudRiskEngine) {

        this.transactionRepository = transactionRepository;
        this.fraudRiskEngine = fraudRiskEngine;
    }

    private static final Logger log = LoggerFactory.getLogger(FraudInvestigationTools.class);

    @Tool("Retrieves the customer's 10 most recent transactions using the customer ID")
    public String getCustomerHistory(
            @P("The unique customer ID") Long customerId) {

        List<Transaction> transactions = transactionRepository
                .findTop10ByCustomerIdOrderByTransactionTimestampDesc(customerId);

        if (transactions.isEmpty()) {
            return "No transaction history found for customer: " + customerId;
        }

        StringBuilder history = new StringBuilder();

        history.append("Recent Customer Transaction History:\n");
        System.out.println("TOOL CALLED: getCustomerHistory(" + customerId + ")");

        log.info(
                "Tool called: getCustomerHistory customerId={}",
                customerId);
        for (Transaction transaction : transactions) {

            history.append("""

                    Transaction Reference: %s
                    Amount: %s %s
                    Merchant: %s
                    Device ID: %s
                    Location: %s
                    Timestamp: %s
                    """.formatted(
                    transaction.getTransactionReference(),
                    transaction.getAmount(),
                    transaction.getCurrency(),
                    transaction.getMerchant(),
                    transaction.getDeviceId(),
                    transaction.getLocation(),
                    transaction.getTransactionTimestamp()));
        }

        return history.toString();
    }

    @Tool("Retrieves the complete transaction details using the transaction reference")
    public String getTransaction(
            @P("The unique transaction reference, for example TXN-1001") String transactionReference) {

        Transaction transaction = transactionRepository
                .findByTransactionReference(transactionReference)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found: " + transactionReference));

        System.out.println(
                "TOOL CALLED: getTransaction(" + transactionReference + ")");
        log.info(
                "Tool called: getTransaction transactionReference={}",
                transactionReference);

        return """
                Transaction Details:
                Transaction Reference: %s
                Customer ID: %s
                Amount: %s
                Currency: %s
                Merchant: %s
                Device ID: %s
                Location: %s
                Transaction Time: %s
                """.formatted(
                transaction.getTransactionReference(),
                transaction.getCustomer().getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getMerchant(),
                transaction.getDeviceId(),
                transaction.getLocation(),
                transaction.getTransactionTimestamp());
    }

    @Tool("""
            Evaluates deterministic fraud rules for a transaction.
            Use this tool when fraud-rule findings or risk indicators
            are required. The returned score is calculated by the
            Java risk engine and must not be modified or recalculated.
            """)
    public String checkFraudRules(
            @P("The transaction reference to evaluate") String transactionReference) {

        Transaction transaction = transactionRepository
                .findByTransactionReference(transactionReference)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found: "
                                + transactionReference));

        RiskAssessment assessment = fraudRiskEngine.evaluate(transaction);

        if (assessment.triggeredRules().isEmpty()) {
            return """
                    Fraud Rule Evaluation:
                    No fraud rules triggered.
                    Deterministic Score: 0
                    Risk Level: LOW
                    """;
        }

        StringBuilder result = new StringBuilder();

        result.append("Fraud Rule Evaluation:\n");
        log.info("Tool called: checkFraudRules transactionReference={}", transactionReference);

        for (TriggeredRule rule : assessment.triggeredRules()) {

            result.append(
                    """

                            Rule: %s
                            Description: %s
                            Score Contribution: %d
                            """.formatted(
                            rule.ruleCode(),
                            rule.description(),
                            rule.score()));
        }

        result.append(
                """

                        Deterministic Score: %d
                        Risk Level: %s
                        """.formatted(
                        assessment.totalScore(),
                        assessment.riskLevel()));

        return result.toString();
    }
}