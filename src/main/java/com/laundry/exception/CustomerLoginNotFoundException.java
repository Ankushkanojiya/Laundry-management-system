package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class CustomerLoginNotFoundException extends BusinessException {
    public CustomerLoginNotFoundException() {
        super("customer login not found", HttpStatus.NOT_FOUND);
    }
}
