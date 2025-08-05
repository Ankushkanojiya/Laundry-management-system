package com.laundry.service;

import com.laundry.dto.PaymentRequest;
import com.laundry.dto.PaymentSummary;
import com.laundry.dto.PaymentTransactionDTO;
import com.laundry.model.Customer;
import com.laundry.model.CustomerAccount;
import com.laundry.model.PaymentTransactions;
import com.laundry.repo.CustomerAccountRepository;
import com.laundry.repo.CustomerRepository;
import com.laundry.repo.PaymentTransactionHistory;
import com.laundry.security.JwtUtil;
import com.laundry.util.ReceiptGenerator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {
    private final CustomerAccountRepository accountRepo;
    private final CustomerRepository customerRepo;
    private final OrderService orderService;
    private final PaymentTransactionHistory transactionRepo;
    @Autowired
    private JwtUtil jwtUtil;

    public List<PaymentSummary> getPaymentSummary(){
        return accountRepo.findCustomersWithBalance();
    }

    @Transactional
    public PaymentTransactionDTO recordPayment(PaymentRequest request){
        // validate customer exists or not
        Customer customer=customerRepo.findById(request.getCustomerId()).orElseThrow(()-> new RuntimeException("Customer not found"));
        // check is there account
        CustomerAccount account=accountRepo.findByCustomer(customer).orElseThrow(()-> new RuntimeException("Account is not found"));

        if (request.getAmount()<=0){
            System.out.println(" Amount should be greater than zero");
        }
        if (request.getAmount() > account.getBalance()){
            System.out.println(" Bahut paise aa gaye hai Lala seth");
        }
        // deduct the balance
        account.setBalance(account.getBalance()-request.getAmount());
        System.out.println(account.getBalance());
        accountRepo.save(account);

        // change the Status to "COMPLETE" OF EVERY ORDER
        if (account.getBalance()==0){
            orderService.completeAllOrders(account.getCustomer());
        }
        return paymentHistory(account, request);

    }

    @Transactional
    public PaymentTransactionDTO paymentHistory(CustomerAccount account, PaymentRequest request){
        PaymentTransactions transactions=new PaymentTransactions();
        transactions.setAccount(account);
        transactions.setAmount(request.getAmount());
        transactions.setTimestamp(LocalDateTime.now());
        PaymentTransactions savedTransaction = transactionRepo.save(transactions);

        try {
            String path= ReceiptGenerator.generateReceipt(savedTransaction);
            savedTransaction.setPdfPath(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new PaymentTransactionDTO(savedTransaction);

    }

    public List<PaymentTransactionDTO> getPaymentHistory(Long customerId) {
        return transactionRepo.findByAccount_CustomerId(customerId)
                .stream()
                .map(PaymentTransactionDTO::new)
                .toList();
    }

    @Transactional
    public String recordCustomerPayment(PaymentRequest request, String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return "The invalid token";
        }
        String token=authHeader.substring(7);
        String phone=jwtUtil.extractPhone(token);

        Customer customer = customerRepo.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        PaymentRequest paymentRequest=new PaymentRequest();
        paymentRequest.setCustomerId(customer.getId());
        paymentRequest.setAmount(request.getAmount());

        recordPayment(paymentRequest);
        return "Payment Successful";
    }
}
