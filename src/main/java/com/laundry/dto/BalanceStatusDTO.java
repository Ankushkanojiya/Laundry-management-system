package com.laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BalanceStatusDTO {
    private double balance;
    private boolean hasPendingPayment;
}