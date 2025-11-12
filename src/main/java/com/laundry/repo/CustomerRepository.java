package com.laundry.repo;

import com.laundry.model.Customer;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    List<Customer> findAll();

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Customer> findByPhoneNumber(String phoneNumber);
    @Query("SELECT c FROM Customer c WHERE LOWER(TRIM(c.email)) = LOWER(TRIM(:email))")
    Optional<Customer> findByEmailIgnoreCase(@Param("email") String email);
    boolean existsByEmail(String email);
    Customer findByEmail(String email);
}
