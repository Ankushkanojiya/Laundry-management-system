package com.laundry.controller;

import com.laundry.dto.PaymentRequest;
import com.laundry.dto.PaymentSummary;
import com.laundry.model.Customer;
import com.laundry.model.CustomerAccount;
import com.laundry.repo.CustomerAccountRepository;
import com.laundry.repo.CustomerRepository;
import com.laundry.service.OrderService;
import com.laundry.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;
    private final CustomerAccountRepository accountRepo;
    private final CustomerRepository customerRepo;
    private final PaymentService paymentService;


    @GetMapping
    public ResponseEntity<List<PaymentSummary>> getPaymentSummary(){
        return ResponseEntity.ok(paymentService.getPaymentSummary());
    }

    @PostMapping
    public ResponseEntity<?> recordPayment(@RequestBody PaymentRequest request) {
        try{
            paymentService.recordPayment(request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
