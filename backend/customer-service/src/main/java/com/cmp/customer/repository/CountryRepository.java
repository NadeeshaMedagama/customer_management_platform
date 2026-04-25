package com.cmp.customer.repository;

import com.cmp.customer.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findByCodeIn(Collection<String> codes);
}

