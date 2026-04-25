package com.cmp.customer.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class BulkUpsertRequest {

    @Valid
    @NotEmpty
    private List<BulkUpsertItem> customers = new ArrayList<>();

    public List<BulkUpsertItem> getCustomers() {
        return customers;
    }

    public void setCustomers(List<BulkUpsertItem> customers) {
        this.customers = customers;
    }
}

