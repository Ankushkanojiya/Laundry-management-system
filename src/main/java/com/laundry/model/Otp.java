package com.laundry.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

    private Long customerId;

    private String otpCode;

    private LocalDateTime expiryTime;
}
