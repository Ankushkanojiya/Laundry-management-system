package com.laundry.exception;

import com.laundry.dto.ErrorResponseDTO;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusiness(BusinessException exce, WebRequest request){
        ErrorResponseDTO errorResponse=ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(exce.getHttpStatus().value())
                .error(exce.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse,exce.getHttpStatus());
    }

}
