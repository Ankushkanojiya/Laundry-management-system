package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class CustomerNotFoundException extends BusinessException{
    public CustomerNotFoundException(Long id){
        super("Customer with id: "+id+" not found", HttpStatus.NOT_FOUND);
    }
}
