package com.cmp.importsvc.dto;

public class ImportSummary {

    private long rowsRead;
    private long rowsAccepted;
    private long rowsRejected;
    private long created;
    private long updated;

    public void incrementRead() {
        rowsRead++;
    }

    public void incrementAccepted() {
        rowsAccepted++;
    }

    public void incrementRejected() {
        rowsRejected++;
    }

    public void addCreated(int value) {
        created += value;
    }

    public void addUpdated(int value) {
        updated += value;
    }

    public long getRowsRead() {
        return rowsRead;
    }

    public long getRowsAccepted() {
        return rowsAccepted;
    }

    public long getRowsRejected() {
        return rowsRejected;
    }

    public long getCreated() {
        return created;
    }

    public long getUpdated() {
        return updated;
    }
}

