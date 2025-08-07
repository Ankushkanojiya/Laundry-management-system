package com.laundry.repo;

import com.laundry.model.CustomerAccount;
import com.laundry.model.PendingCustomerPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingCustomerPaymentRepository extends JpaRepository<PendingCustomerPayment,Long> {
    List<PendingCustomerPayment> findByAccount(CustomerAccount account);


}
