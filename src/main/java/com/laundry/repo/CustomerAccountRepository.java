package com.laundry.repo;

import com.laundry.dto.PaymentSummary;
import com.laundry.model.Customer;
import com.laundry.model.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {

    Optional<CustomerAccount> findByCustomer(Customer customer);

    void deleteByCustomer(Customer customer);

    @Query("SELECT NEW com.laundry.dto.PaymentSummary(" +
            "c.id, c.name, " +
            "a.balance) " +  // Directly use account balance
            "FROM CustomerAccount a " +
            "JOIN a.customer c " +
            "WHERE a.balance > 0")  // Only show customers with balance due
    List<PaymentSummary> findCustomersWithBalance();
}
