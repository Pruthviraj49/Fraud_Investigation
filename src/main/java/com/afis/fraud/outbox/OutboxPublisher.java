package com.afis.fraud.outbox;

import com.afis.fraud.dto.TransactionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxPublisher {

    private static final String TOPIC = "transaction-events";

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final int MAX_RETRIES = 5;

    public OutboxPublisher(
            OutboxEventRepository repository,
            KafkaTemplate<String, TransactionEvent> kafkaTemplate,
            ObjectMapper objectMapper) {

        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 2000)
    public void publishPendingEvents() {

        var events = repository
                .findTop100ByStatusOrderByCreatedAtAsc(
                        OutboxStatus.PENDING);

        for (OutboxEvent event : events) {
            publish(event);
        }
    }

    private void publish(OutboxEvent outboxEvent) {

        try {

            TransactionEvent transactionEvent = objectMapper.readValue(
                    outboxEvent.getPayload(),
                    TransactionEvent.class);

            kafkaTemplate
                    .send(
                            TOPIC,
                            outboxEvent.getAggregateId(),
                            transactionEvent)
                    .whenComplete((result, exception) -> {

                        if (exception == null) {

                            markPublished(outboxEvent);

                        } else {

                            handleFailure(outboxEvent, exception);
                        }
                    });

        } catch (Exception e) {

            handleFailure(outboxEvent, e);
        }
    }

    private void markPublished(
            OutboxEvent event) {

        event.markPublished();

        repository.save(event);
    }

    private void handleFailure(
            OutboxEvent event,
            Throwable exception) {

        event.incrementRetryCount();

        if (!event.canRetry()) {

            event.markFailed();

            System.err.println(
                    "Outbox event permanently failed: "
                            + event.getId());

        } else {

            System.err.println(
                    "Outbox event publish failed. "
                            + "Retry count: "
                            + event.getRetryCount());
        }

        repository.save(event);
    }
}