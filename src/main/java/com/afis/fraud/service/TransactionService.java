package com.afis.fraud.service;

import com.afis.fraud.dto.CreateTransactionRequest;
import com.afis.fraud.dto.TransactionEvent;
import com.afis.fraud.dto.TransactionResponse;
import com.afis.fraud.entity.Customer;
import com.afis.fraud.entity.Transaction;
import com.afis.fraud.exception.ResourceNotFoundException;
import com.afis.fraud.outbox.OutboxEvent;
import com.afis.fraud.outbox.OutboxEventService;
import com.afis.fraud.repository.CustomerRepository;
import com.afis.fraud.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TransactionService {

        private final TransactionRepository transactionRepository;
        private final CustomerRepository customerRepository;
        // private final TransactionEventProducer transactionEventProducer;
        private final OutboxEventService outboxEventService;

        public TransactionService(
                        TransactionRepository transactionRepository,
                        CustomerRepository customerRepository, OutboxEventService outboxEventService) {

                this.transactionRepository = transactionRepository;
                this.customerRepository = customerRepository;
                this.outboxEventService = outboxEventService;
        }

        @Transactional
        public TransactionResponse createTransaction(
                        CreateTransactionRequest request) {

                Customer customer = customerRepository
                                .findByCustomerReference(request.getCustomerReference())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Customer not found: "
                                                                + request.getCustomerReference()));

                Transaction transaction = Transaction.create(
                                generateTransactionReference(),
                                customer,
                                request.getAmount(),
                                request.getCurrency(),
                                request.getMerchant(),
                                request.getDeviceId(),
                                request.getLocation(),
                                request.getTransactionTimestamp());

                Transaction savedTransaction = transactionRepository.save(transaction);

                TransactionEvent event = new TransactionEvent(
                                savedTransaction.getTransactionReference(),
                                savedTransaction.getCustomer()
                                                .getCustomerReference(),
                                savedTransaction.getAmount(),
                                savedTransaction.getCurrency(),
                                savedTransaction.getMerchant(),
                                savedTransaction.getDeviceId(),
                                savedTransaction.getLocation(),
                                savedTransaction.getTransactionTimestamp());

                OutboxEvent outboxEvent = outboxEventService.createTransactionEvent(event);

                outboxEventService.save(outboxEvent);

                return toResponse(savedTransaction);
        }

        @Transactional(readOnly = true)
        public TransactionResponse getTransaction(
                        String transactionReference) {

                Transaction transaction = transactionRepository
                                .findByTransactionReference(transactionReference)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Transaction not found: "
                                                                + transactionReference));

                return toResponse(transaction);
        }

        private String generateTransactionReference() {

                return "TXN-" +
                                UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                                                .toUpperCase();
        }

        private TransactionResponse toResponse(
                        Transaction transaction) {

                TransactionResponse response = new TransactionResponse();

                response.setTransactionReference(
                                transaction.getTransactionReference());

                response.setCustomerReference(
                                transaction.getCustomer()
                                                .getCustomerReference());

                response.setAmount(transaction.getAmount());
                response.setCurrency(transaction.getCurrency());
                response.setMerchant(transaction.getMerchant());
                response.setDeviceId(transaction.getDeviceId());
                response.setLocation(transaction.getLocation());

                response.setTransactionTimestamp(
                                transaction.getTransactionTimestamp());

                response.setCreatedAt(
                                transaction.getCreatedAt());

                return response;
        }
}