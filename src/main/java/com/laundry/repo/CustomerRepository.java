package com.laundry.repo;

import com.laundry.model.Customer;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    List<Customer> findAll();

    boolean existsByPhoneNumber(String phoneNumber);
}
