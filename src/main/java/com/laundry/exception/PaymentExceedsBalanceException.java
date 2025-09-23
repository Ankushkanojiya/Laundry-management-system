package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class PaymentExceedsBalanceException extends BusinessException {
    public PaymentExceedsBalanceException() {
        super("Amount cannot exceed the balance", HttpStatus.BAD_REQUEST);
    }
}
