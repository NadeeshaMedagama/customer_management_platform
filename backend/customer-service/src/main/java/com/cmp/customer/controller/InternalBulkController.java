package com.cmp.customer.controller;

import com.cmp.customer.dto.BulkUpsertRequest;
import com.cmp.customer.dto.BulkUpsertResponse;
import com.cmp.customer.service.CustomerService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/internal/customers")
@Validated
public class InternalBulkController {

    private final CustomerService customerService;

    public InternalBulkController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/bulk-upsert")
    public BulkUpsertResponse bulkUpsert(@Valid @RequestBody BulkUpsertRequest request) {
        return customerService.bulkUpsert(request);
    }
}

