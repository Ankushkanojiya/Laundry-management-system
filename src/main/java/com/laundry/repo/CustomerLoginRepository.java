package com.laundry.repo;

import com.laundry.model.Customer;
import com.laundry.model.CustomerLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerLoginRepository extends JpaRepository<CustomerLogin,Long>{

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<CustomerLogin> findByPhoneNumber(String phoneNumber);

    Optional<CustomerLogin> findByCustomer(Customer customer);
}