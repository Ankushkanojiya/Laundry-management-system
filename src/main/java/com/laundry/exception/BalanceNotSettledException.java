package com.laundry.exception;

import org.springframework.http.HttpStatus;

public class BalanceNotSettledException extends BusinessException {
    public BalanceNotSettledException(Long id, double balance) {

        super("Cannot delete the customer with ID: "+id+". Please settle the balance first (Remaining balance: ₹ "+balance, HttpStatus.BAD_REQUEST);
    }
}
