package com.cmp.customer.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_nic", columnList = "nic_number", unique = true)
})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "nic_number", nullable = false, unique = true, length = 50)
    private String nicNumber;

    @ElementCollection
    @CollectionTable(name = "customer_mobile_numbers", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "mobile_number", length = 20)
    private Set<String> mobileNumbers = new HashSet<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "customer_family_members",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "family_member_id"))
    private Set<Customer> familyMembers = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    public Set<String> getMobileNumbers() {
        return mobileNumbers;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public Set<Customer> getFamilyMembers() {
        return familyMembers;
    }
}

