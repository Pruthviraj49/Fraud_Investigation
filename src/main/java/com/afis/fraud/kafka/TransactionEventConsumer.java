package com.afis.fraud.kafka;

import com.afis.fraud.dto.TransactionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventConsumer {

    @KafkaListener(topics = "transaction-events", groupId = "afis-investigation-group")
    public void consume(TransactionEvent event) {

        System.out.println(
                "Received transaction event: "
                        + event.getTransactionReference());

        System.out.println(
                "Amount: "
                        + event.getAmount()
                        + " "
                        + event.getCurrency());

        System.out.println(
                "Merchant: "
                        + event.getMerchant());
    }
}