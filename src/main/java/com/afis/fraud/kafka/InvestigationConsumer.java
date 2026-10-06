package com.afis.fraud.kafka;

import com.afis.fraud.dto.TransactionEvent;
import com.afis.fraud.service.InvestigationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InvestigationConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(InvestigationConsumer.class);

    private final InvestigationService investigationService;

    public InvestigationConsumer(
            InvestigationService investigationService) {
        this.investigationService = investigationService;
    }

    @KafkaListener(
            topics = "transaction-events",
            groupId = "investigation-group"
    )
    public void consume(TransactionEvent event) {

        log.info(
                "Received transaction event: {}",
                event.getTransactionReference()
        );

        investigationService.investigate(
                event.getTransactionReference()
        );
    }
}