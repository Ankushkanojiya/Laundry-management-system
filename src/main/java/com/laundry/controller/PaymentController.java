package com.laundry.controller;

import com.laundry.dto.PaymentRequest;
import com.laundry.dto.PaymentSummary;
import com.laundry.dto.PaymentTransactionDTO;
import com.laundry.dto.PendingPaymentDTO;
import com.laundry.model.Customer;
import com.laundry.model.CustomerAccount;
import com.laundry.model.PaymentTransactions;
import com.laundry.model.PendingCustomerPayment;
import com.laundry.repo.CustomerAccountRepository;
import com.laundry.repo.CustomerRepository;
import com.laundry.repo.PaymentTransactionHistory;
import com.laundry.repo.PendingCustomerPaymentRepository;
import com.laundry.service.OrderService;
import com.laundry.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;
    private final CustomerAccountRepository accountRepo;
    private final CustomerRepository customerRepo;
    private final PaymentService paymentService;
    private final PaymentTransactionHistory transactionRepo;
    private final PendingCustomerPaymentRepository pendingRepo;


    @GetMapping
    public ResponseEntity<List<PaymentSummary>> getPaymentSummary(){
        return ResponseEntity.ok(paymentService.getPaymentSummary());
    }

    @PostMapping
    public ResponseEntity<?> recordPayment(@RequestBody PaymentRequest request) {
        try{
            PaymentTransactionDTO paymentDto=paymentService.recordPayment(request);
            return ResponseEntity.ok(paymentDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @GetMapping("/{customerId}/history")
    public ResponseEntity<List<PaymentTransactionDTO>> getPaymentHistory(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(customerId));
    }

    @GetMapping("/{customerId}/balance")
    public ResponseEntity<Double> getCustomerBalance(@PathVariable Long customerId){
        Customer customer=customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerAccount account=accountRepo.findByCustomer(customer)
                .orElseThrow(()-> new RuntimeException("Account not found"));

        return ResponseEntity.ok(account.getBalance());
    }
//    @PostMapping("/customer")
//    public ResponseEntity<PaymentTransactionDTO> recordCustomerPayment(@RequestBody PaymentRequest request,@RequestHeader("Authorization") String authHeader){
//        return ResponseEntity.ok(paymentService.recordCustomerPayment(request,authHeader));
//    }
    @PostMapping("/pending")
    public ResponseEntity<PaymentTransactionDTO> recordPendingPayments(@RequestBody PaymentRequest request, @RequestHeader("Authorization") String authHeader){
        try{
            PaymentTransactionDTO dto=paymentService.recordPendingCustomerPayment(request,authHeader);
            return ResponseEntity.ok(dto);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PaymentTransactionDTO>> getAllPendingPayments(){
        List<PaymentTransactionDTO> pendingPayments=paymentService.getAllPendingPayments();
        return ResponseEntity.ok(pendingPayments);

    }

    @PatchMapping("/{transactionId}/verify")
    public ResponseEntity<?> verifyPendingPayments(@PathVariable Long transactionId){
        try {
            PaymentTransactionDTO verifyPayment=paymentService.verifyAndConfirmPendingPayments(transactionId);
            return ResponseEntity.ok(verifyPayment);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{transactionId}/reject")
    public ResponseEntity<?> rejectPendingPayments(@PathVariable Long transactionId){
        try{
            PendingPaymentDTO rejectPayment=paymentService.rejectPendingCustomerPayment(transactionId);
            return ResponseEntity.ok(rejectPayment);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
