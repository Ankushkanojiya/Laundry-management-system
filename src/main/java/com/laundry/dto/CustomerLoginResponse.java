package com.laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerLoginResponse {
    private String message;
    private Long customerId;
    private String name;
    private String phone;
}
