package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaymentAmountException extends BusinessException{
    public InvalidPaymentAmountException(){
        super("The amount should be greater than zero", HttpStatus.BAD_REQUEST);
    }
}
