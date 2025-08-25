package com.laundry.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.laundry.model.PaymentTransactions;
import com.laundry.model.PendingCustomerPayment;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class PaymentTransactionDTO {
    private Long transactionId;
    private String customerName;
    private double amount;
    private LocalDateTime timestamp;


    public PaymentTransactionDTO(PaymentTransactions transaction) {
        this.transactionId = transaction.getTransactionId();
        this.amount = transaction.getAmount();
        this.timestamp = transaction.getTimestamp();


        if (transaction.getAccount() != null &&
                transaction.getAccount().getCustomer() != null) {
            this.customerName = transaction.getAccount().getCustomer().getName();
        }
    }

    public PaymentTransactionDTO(PendingCustomerPayment pending) {
        this.transactionId = pending.getId();
        this.customerName = pending.getAccount().getCustomer().getName();
        this.amount = pending.getAmount();
        this.timestamp = pending.getTimestamp();


    }

}
