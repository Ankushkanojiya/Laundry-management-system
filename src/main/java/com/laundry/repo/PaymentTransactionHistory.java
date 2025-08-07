package com.laundry.repo;

import com.laundry.model.PaymentTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionHistory extends JpaRepository<PaymentTransactions,Long> {

    List<PaymentTransactions> findByAccount_CustomerId(Long customerId);

    Optional<PaymentTransactions> findById(Long transactionId);
}
