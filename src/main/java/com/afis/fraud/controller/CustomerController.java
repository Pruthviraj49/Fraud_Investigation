package com.afis.fraud.controller;

import com.afis.fraud.entity.Customer;
import com.afis.fraud.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@RequestBody Customer request) {
        Customer customer = Customer.create(
                request.getCustomerReference(),
                request.getCountry(),
                request.getStatus());

        return customerRepository.save(customer);
    }
}