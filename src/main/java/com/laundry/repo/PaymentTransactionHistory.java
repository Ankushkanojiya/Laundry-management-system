package com.laundry.repo;

import com.laundry.model.PaymentTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionHistory extends JpaRepository<PaymentTransactions,Long> {

    List<PaymentTransactions> findByAccount_CustomerId(Long customerId);
}
