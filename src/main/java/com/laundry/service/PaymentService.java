package com.laundry.service;

import com.laundry.dto.*;
import com.laundry.exception.*;
import com.laundry.model.Customer;
import com.laundry.model.CustomerAccount;
import com.laundry.model.PaymentTransactions;
import com.laundry.model.PendingCustomerPayment;
import com.laundry.repo.CustomerAccountRepository;
import com.laundry.repo.CustomerRepository;
import com.laundry.repo.PaymentTransactionHistory;
import com.laundry.repo.PendingCustomerPaymentRepository;
import com.laundry.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {
    private final CustomerAccountRepository accountRepo;
    private final CustomerRepository customerRepo;
    private final OrderService orderService;
    private final PaymentTransactionHistory transactionRepo;
    private final PendingCustomerPaymentRepository pendingRepo;
    private final ReceiptGeneratorPdf receiptGeneratorPdf;
    @Autowired
    private JwtUtil jwtUtil;

    public List<PaymentSummary> getPaymentSummary(){
        return accountRepo.findCustomersWithBalance();
    }

    public List<PaymentTransactionDTO> getPaymentHistory(Long customerId) {
        return transactionRepo.findByAccount_CustomerId(customerId)
                .stream()
                .map(PaymentTransactionDTO::new)
                .toList();
    }

    @Transactional
    public PaymentTransactionDTO recordPayment(PaymentRequest request){
        // validate customer exists or not
        Customer customer=customerRepo.findById(request.getCustomerId()).orElseThrow(()-> new CustomerNotFoundException(request.getCustomerId()));
        // check is there account
        CustomerAccount account=accountRepo.findByCustomer(customer).orElseThrow(()-> new CustomerNotFoundException(request.getCustomerId()));

        if (request.getAmount()<=0) throw new InvalidPaymentAmountException();

        if (request.getAmount() > account.getBalance()) throw new PaymentExceedsBalanceException();

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

        return new PaymentTransactionDTO(savedTransaction);

    }

//    @Transactional
//    public PaymentTransactionDTO recordCustomerPayment(PaymentRequest request, String authHeader) {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")){
//            return null;
//        }
//        String token=authHeader.substring(7);
//        String phone=jwtUtil.extractPhone(token);
//
//        Customer customer = customerRepo.findByPhoneNumber(phone)
//                .orElseThrow(() -> new RuntimeException("Customer not found"));
//
//        PaymentRequest paymentRequest=new PaymentRequest();
//        paymentRequest.setCustomerId(customer.getId());
//        paymentRequest.setAmount(request.getAmount());
//
//        return recordPayment(paymentRequest);
//
//
//    }
    @Transactional
    public PaymentTransactionDTO recordPendingCustomerPayment(PaymentRequest request,String authHeader){
        if (authHeader ==null || !authHeader.startsWith("Bearer ")){
            throw new RuntimeException("invalid token");
        }

        String token=authHeader.substring(7);
        String phone=jwtUtil.extractPhone(token);

        Customer customer = customerRepo.findByPhoneNumber(phone)
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        CustomerAccount account=accountRepo.findByCustomer(customer).orElseThrow(()-> new CustomerNotFoundException(request.getCustomerId()));

        boolean isPending = pendingRepo.existsByAccountAndCustomerPaymentStatus(account, PendingCustomerPayment.PaymentStatus.PENDING);

        if (isPending){
            throw new PendingPaymentExistsException();
        }

        PendingCustomerPayment pending = PendingCustomerPayment.builder()
                .account(account)
                .amount(request.getAmount())
                .timestamp(LocalDateTime.now())
                .customerPaymentStatus(PendingCustomerPayment.PaymentStatus.PENDING)
                .build();
        pending=pendingRepo.save(pending);
        return new PaymentTransactionDTO(pending);
    }

    public PaymentTransactionDTO verifyAndConfirmPendingPayments(Long transactionId) {

        PendingCustomerPayment pending=pendingRepo.findById(transactionId)
                .orElseThrow(()->new PaymentDetailsNotFound(transactionId));

        pending.setCustomerPaymentStatus(PendingCustomerPayment.PaymentStatus.CONFIRMED);


        CustomerAccount account=pending.getAccount();

        // this is where we put the payment deduction

        double currentBalance=account.getBalance();
        double payAmount=pending.getAmount();

        account.setBalance(currentBalance-payAmount);

        PaymentTransactions confirmPayment=new PaymentTransactions();
        confirmPayment.setAccount(pending.getAccount());
        confirmPayment.setAmount(pending.getAmount());
        confirmPayment.setTimestamp(pending.getTimestamp());


        PaymentTransactions saveTransaction=transactionRepo.save(confirmPayment);



        accountRepo.save(account);
        transactionRepo.save(saveTransaction);
        if (account.getBalance()==0){
            orderService.completeAllOrders(account.getCustomer());
        }

        pendingRepo.delete(pending);

        return new PaymentTransactionDTO(saveTransaction);
    }

    public PendingPaymentDTO rejectPendingCustomerPayment(Long transactionId){
        PendingCustomerPayment pending = pendingRepo.findById(transactionId)
                .orElseThrow(() -> new PaymentDetailsNotFound(transactionId));

        if (pending.getCustomerPaymentStatus() != PendingCustomerPayment.PaymentStatus.PENDING){
            throw new RuntimeException("Only pending payments can be rejected");
        }
        pending.setCustomerPaymentStatus(PendingCustomerPayment.PaymentStatus.REJECTED);
        pendingRepo.delete(pending);

        return new PendingPaymentDTO(pending);
    }


    public List<PaymentTransactionDTO> getAllPendingPayments() {
        List<PendingCustomerPayment> pendingPayments = pendingRepo.findAll();
        List<PaymentTransactionDTO> pendingList = new ArrayList<>();

        for (PendingCustomerPayment list : pendingPayments) {
            pendingList.add(new PaymentTransactionDTO(list));
        }
        return pendingList;
    }

    public BalanceStatusDTO getCustomerBalance(Long customerId){
        Customer customer=customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerAccount account=accountRepo.findByCustomer(customer)
                .orElseThrow(()-> new RuntimeException("Account not found"));

        double balance= account.getBalance();
        boolean isPending=pendingRepo.existsByAccountAndCustomerPaymentStatus(account, PendingCustomerPayment.PaymentStatus.PENDING);

        return new BalanceStatusDTO(balance,isPending);
    }

}
