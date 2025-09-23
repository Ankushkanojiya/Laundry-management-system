package com.laundry.repo;

import com.laundry.model.PaymentTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentTransactionHistory extends JpaRepository<PaymentTransactions,Long> {

    List<PaymentTransactions> findByAccount_CustomerId(Long customerId);

    Optional<PaymentTransactions> findById(Long transactionId);

    @Query("SELECT COALESCE(SUM(p.amount),0) FROM PaymentTransactions p WHERE DATE(p.timestamp)= :date")
    double sumOfRevenueToday(@Param("date")LocalDate date);
}
