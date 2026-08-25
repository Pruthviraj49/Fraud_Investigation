package com.afis.fraud.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_reference", nullable = false, unique = true, length = 50)
    private String customerReference;

    @Column(nullable = false, length = 10)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Customer() {
    }

    public static Customer create(String customerReference, String country, CustomerStatus status) {
        Customer customer = new Customer();
        customer.setCustomerReference(customerReference);
        customer.setCountry(country);
        customer.setStatus(status != null ? status : CustomerStatus.ACTIVE);
        customer.setCreatedAt(OffsetDateTime.now());
        return customer;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}