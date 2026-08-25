package com.afis.fraud.controller;

import com.afis.fraud.dto.CreateTransactionRequest;
import com.afis.fraud.dto.TransactionResponse;
import com.afis.fraud.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {

        return transactionService.createTransaction(request);
    }

    @GetMapping("/{transactionReference}")
    public TransactionResponse getTransaction(
            @PathVariable String transactionReference) {

        return transactionService
                .getTransaction(transactionReference);
    }
}