package com.afis.fraud.ai;

import com.afis.fraud.entity.*;
import com.afis.fraud.repository.*;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FraudInvestigationTools {

    private final TransactionRepository transactionRepository;

    public FraudInvestigationTools(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

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
        "TOOL CALLED: getTransaction(" + transactionReference + ")"
        );

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
}