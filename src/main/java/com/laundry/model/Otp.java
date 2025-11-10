package com.laundry.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
public class Otp {
    @Id
    private String id;

    private String customerId;

    private String otpCode;

    private LocalDateTime expiryTime;
}
