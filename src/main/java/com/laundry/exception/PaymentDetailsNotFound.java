package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class PaymentDetailsNotFound extends BusinessException {
    public PaymentDetailsNotFound(Long transactionId) {
        super("No payments details associated with transaction id: "+transactionId, HttpStatus.NOT_FOUND);
    }
}
