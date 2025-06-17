package com.laundry.dto;

import lombok.Data;

@Data
public class CustomerRegisterRequest {
    private String phoneNumber;
    private String password;
}
