package com.laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReceiptDataDTO {
    private Long transactionId;
    private String customerName;
    private LocalDateTime paymentDate;
    private double amount;
}
