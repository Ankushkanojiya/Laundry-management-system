package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class PaymentTransactionIdNotFound extends BusinessException {
    public PaymentTransactionIdNotFound(Long transactionId) {

      super("Transaction ID: "+transactionId+" is not found", HttpStatus.NOT_FOUND);
    }
}
