package com.laundry.controller;

import com.laundry.dto.PaymentSummary;
import com.laundry.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<PaymentSummary>> getPaymentSummary(){
        return ResponseEntity.ok(orderService.getPaymentSummary());
    }
}
