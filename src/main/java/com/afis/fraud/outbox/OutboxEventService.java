package com.afis.fraud.outbox;

import com.afis.fraud.dto.TransactionEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class OutboxEventService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxEventService(
            OutboxEventRepository repository,
            ObjectMapper objectMapper) {

        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public OutboxEvent createTransactionEvent(
            TransactionEvent event) {

        try {

            String payload = objectMapper.writeValueAsString(event);

            return OutboxEvent.create(
                    "TRANSACTION_CREATED",
                    "TRANSACTION",
                    event.getTransactionReference(),
                    payload);

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to serialize transaction event",
                    e);
        }
    }

    public void save(OutboxEvent event) {
        repository.save(event);
    }
}