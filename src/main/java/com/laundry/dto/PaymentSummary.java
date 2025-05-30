package com.laundry.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class PaymentSummary {
    private Long customerId;
    private String customerName;
    private Long totalClothes;
    private Double totalAmount;
}
