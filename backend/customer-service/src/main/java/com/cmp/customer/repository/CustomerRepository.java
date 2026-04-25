package com.cmp.customer.repository;

import com.cmp.customer.entity.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNicNumber(String nicNumber);

    boolean existsByNicNumberAndIdNot(String nicNumber, Long id);

    List<Customer> findByNicNumberIn(Collection<String> nicNumbers);

    @Override
    @EntityGraph(attributePaths = {"addresses", "addresses.city", "addresses.country", "familyMembers"})
    Optional<Customer> findById(Long id);
}
