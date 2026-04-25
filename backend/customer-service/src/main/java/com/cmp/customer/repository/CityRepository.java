package com.cmp.customer.repository;

import com.cmp.customer.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByCodeIn(Collection<String> codes);
}

