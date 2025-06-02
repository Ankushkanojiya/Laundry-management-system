package com.laundry.controller;

import com.laundry.dto.PaymentRequest;
import com.laundry.dto.PaymentSummary;
import com.laundry.model.Customer;
import com.laundry.model.CustomerAccount;
import com.laundry.repo.CustomerAccountRepository;
import com.laundry.repo.CustomerRepository;
import com.laundry.service.OrderService;
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


    @GetMapping
    public ResponseEntity<List<PaymentSummary>> getPaymentSummary(){
        return ResponseEntity.ok(accountRepo.findCustomersWithBalance());
    }

    @PostMapping
    public ResponseEntity<?> recordPayment(@RequestBody PaymentRequest request) {
        // 1. Validate customer exists
        Customer customer =customerRepo.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 2. Get account (throw error if not found)
        CustomerAccount account = accountRepo.findByCustomer(customer)
                .orElseThrow(() -> new RuntimeException("Customer account not found"));

        // 3. Validate payment amount
        if (request.getAmount() <= 0) {
            System.out.println("Amount must be positive");
            return ResponseEntity.badRequest().body("Amount must be positive");
        }
        if (request.getAmount() > account.getBalance()) {
            System.out.println("Payment exceeds balance");
            return ResponseEntity.badRequest().body("Payment exceeds balance");
        }

        // 4. Deduct from balance
        account.setBalance(account.getBalance() - request.getAmount());
        System.out.println(account.getBalance());
        accountRepo.save(account);

        return ResponseEntity.ok().build();
    }
}
