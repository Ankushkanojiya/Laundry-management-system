package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class InvalidPhoneAndPassword extends BusinessException {
    public InvalidPhoneAndPassword() {
        super("Invalid phone Number and Password", HttpStatus.BAD_REQUEST);
    }
}
