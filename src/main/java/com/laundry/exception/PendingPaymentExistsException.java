package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class PendingPaymentExistsException extends BusinessException {
    public PendingPaymentExistsException(String message) {

        super("You already have a payment pending for verification. Please wait for admin approval.", HttpStatus.BAD_REQUEST);
    }
}
