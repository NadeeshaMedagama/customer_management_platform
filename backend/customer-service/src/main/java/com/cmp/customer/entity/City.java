package com.cmp.customer.entity;

import javax.persistence.*;

@Entity
@Table(name = "cities", indexes = @Index(name = "idx_city_code", columnList = "code", unique = true))
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

