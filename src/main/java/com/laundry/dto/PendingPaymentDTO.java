package com.laundry.dto;

import com.laundry.model.PendingCustomerPayment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PendingPaymentDTO {

    private Long id;
    private String customerName;
    private double amount;
    private LocalDateTime timestamp;
    private String status;

    public PendingPaymentDTO(PendingCustomerPayment pendingCustomerPayment){
        this.id= pendingCustomerPayment.getId();
        this.amount= pendingCustomerPayment.getAmount();
        this.timestamp=pendingCustomerPayment.getTimestamp();
        this.status=pendingCustomerPayment.getCustomerPaymentStatus().name();

        if (pendingCustomerPayment.getAccount() != null && pendingCustomerPayment.getAccount().getCustomer() != null){
            this.customerName=pendingCustomerPayment.getAccount().getCustomer().getName();
        }
    }
}
