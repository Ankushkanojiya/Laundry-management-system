package com.laundry.service;

import com.laundry.dto.PaymentRequest;
import com.laundry.dto.PaymentSummary;
import com.laundry.model.Customer;
import com.laundry.model.CustomerAccount;
import com.laundry.repo.CustomerAccountRepository;
import com.laundry.repo.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {
    private final CustomerAccountRepository accountRepo;
    private final CustomerRepository customerRepo;

    public List<PaymentSummary> getPaymentSummary(){
        return accountRepo.findCustomersWithBalance();
    }

    public void recordPayment(PaymentRequest request){
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

    }

}
