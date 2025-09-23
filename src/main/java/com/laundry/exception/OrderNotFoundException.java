package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BusinessException{
    public OrderNotFoundException(Long orderId){
        super("Order id: "+orderId+" is not found", HttpStatus.NOT_FOUND);
    }
}
