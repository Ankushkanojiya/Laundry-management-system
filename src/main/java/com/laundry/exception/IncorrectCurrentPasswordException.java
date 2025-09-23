package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class IncorrectCurrentPasswordException extends BusinessException {
    public IncorrectCurrentPasswordException() {

      super("Incorrect current Password", HttpStatus.BAD_REQUEST);
    }
}
