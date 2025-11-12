package com.laundry.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VerifyOtpRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(max = 6,min = 6,message = "OTP must be 6 digits")
    private String otpCode;

    @NotBlank
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String newPassword;
}
