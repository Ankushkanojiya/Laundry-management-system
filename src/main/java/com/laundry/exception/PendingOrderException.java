package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class PendingOrderException extends BusinessException {

    public PendingOrderException() {
        super("Customer has pending orders that must be completed", HttpStatus.CONFLICT);
    }
}
