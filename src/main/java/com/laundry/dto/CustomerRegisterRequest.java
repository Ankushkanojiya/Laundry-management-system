package com.laundry.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerRegisterRequest {
    private String phoneNumber;
    @NotBlank(message = "Email is required for registration ")
    @Email(message = "Please enter a valid email address")
    private String email;
    private String password;
}
