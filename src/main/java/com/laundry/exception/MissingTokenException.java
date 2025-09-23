package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class MissingTokenException extends BusinessException {
    public MissingTokenException() {

      super("Missing or Invalid authentication token", HttpStatus.UNAUTHORIZED);
    }
}
