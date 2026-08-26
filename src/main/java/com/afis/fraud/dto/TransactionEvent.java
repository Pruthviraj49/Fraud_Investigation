package com.afis.fraud.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransactionEvent {

    private String transactionReference;
    private String customerReference;
    private BigDecimal amount;
    private String currency;
    private String merchant;
    private String deviceId;
    private String location;
    private OffsetDateTime transactionTimestamp;

    public TransactionEvent() {
    }

    public TransactionEvent(
            String transactionReference,
            String customerReference,
            BigDecimal amount,
            String currency,
            String merchant,
            String deviceId,
            String location,
            OffsetDateTime transactionTimestamp) {

        this.transactionReference = transactionReference;
        this.customerReference = customerReference;
        this.amount = amount;
        this.currency = currency;
        this.merchant = merchant;
        this.deviceId = deviceId;
        this.location = location;
        this.transactionTimestamp = transactionTimestamp;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public OffsetDateTime getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(OffsetDateTime transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }
}