package com.cmp.customer.dto;

public class BulkUpsertResponse {

    private int createdCount;
    private int updatedCount;

    public BulkUpsertResponse() {
    }

    public BulkUpsertResponse(int createdCount, int updatedCount) {
        this.createdCount = createdCount;
        this.updatedCount = updatedCount;
    }

    public int getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(int createdCount) {
        this.createdCount = createdCount;
    }

    public int getUpdatedCount() {
        return updatedCount;
    }

    public void setUpdatedCount(int updatedCount) {
        this.updatedCount = updatedCount;
    }
}


