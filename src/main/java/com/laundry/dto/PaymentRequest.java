package com.laundry.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long customerId;
    private double amount;
}
