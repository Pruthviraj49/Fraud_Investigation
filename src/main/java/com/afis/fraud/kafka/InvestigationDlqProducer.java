package com.afis.fraud.kafka;

import com.afis.fraud.dto.TransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InvestigationDlqProducer {

    private static final String DLQ_TOPIC = "investigation-dlq";

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public InvestigationDlqProducer(
            KafkaTemplate<String, TransactionEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TransactionEvent event) {

        kafkaTemplate.send(
                DLQ_TOPIC,
                event.getTransactionReference(),
                event);
    }
}