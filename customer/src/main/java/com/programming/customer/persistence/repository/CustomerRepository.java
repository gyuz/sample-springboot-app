package com.programming.customer.persistence.repository;

import com.programming.customer.persistence.model.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    List<Customer> findAll();

    @Query("SELECT COUNT(1) FROM Customer where id = :customerId")
    int countCustomerwithId(@Param("customerId") Long customerId);
}
