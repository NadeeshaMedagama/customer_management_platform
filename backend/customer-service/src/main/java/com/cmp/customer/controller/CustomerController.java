package com.cmp.customer.controller;

import com.cmp.customer.dto.CustomerRequest;
import com.cmp.customer.dto.CustomerResponse;
import com.cmp.customer.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public CustomerResponse create(@Valid @RequestBody CustomerRequest request) {
        return customerService.create(request);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return customerService.update(id, request);
    }

    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable Long id) {
        return customerService.get(id);
    }

    @GetMapping
    public Page<CustomerResponse> list(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return customerService.list(PageRequest.of(page, Math.min(size, 200)));
    }
}

