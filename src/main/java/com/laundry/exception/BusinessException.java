package com.laundry.exception;

import lombok.Getter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BusinessException extends RuntimeException{

    private final HttpStatus httpStatus;

    protected BusinessException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus=httpStatus;
    }

}
