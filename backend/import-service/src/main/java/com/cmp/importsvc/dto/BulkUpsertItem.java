package com.cmp.importsvc.dto;

import java.time.LocalDate;

public class BulkUpsertItem {

    private String name;
    private LocalDate dateOfBirth;
    private String nicNumber;

    public BulkUpsertItem() {
    }

    public BulkUpsertItem(String name, LocalDate dateOfBirth, String nicNumber) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.nicNumber = nicNumber;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNicNumber() {
        return nicNumber;
    }
}

