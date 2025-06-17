package com.laundry.dto;

import lombok.Data;

@Data
public class CustomerLoginRequest {
    private String phoneNumber;
    private String password;
}
