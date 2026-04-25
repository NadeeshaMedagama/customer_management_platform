package com.cmp.importsvc.dto;

import java.util.ArrayList;
import java.util.List;

public class BulkUpsertRequest {

    private List<BulkUpsertItem> customers = new ArrayList<BulkUpsertItem>();

    public BulkUpsertRequest() {
    }

    public BulkUpsertRequest(List<BulkUpsertItem> customers) {
        this.customers = customers;
    }

    public List<BulkUpsertItem> getCustomers() {
        return customers;
    }
}

