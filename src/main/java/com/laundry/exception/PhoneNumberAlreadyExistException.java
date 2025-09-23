package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class PhoneNumberAlreadyExistException extends BusinessException{

    public PhoneNumberAlreadyExistException(String phoneNumber){
        super("Phone Number"+phoneNumber+" already exist",HttpStatus.CONFLICT);
    }
}
