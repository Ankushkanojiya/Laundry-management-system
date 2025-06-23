package com.laundry.service;

import com.laundry.dto.DueCustomers;
import com.laundry.dto.InsightResponse;
import com.laundry.dto.TopCustomers;
import com.laundry.model.Customer;
import com.laundry.model.CustomerAccount;
import com.laundry.model.PaymentTransactions;
import com.laundry.repo.CustomerAccountRepository;
import com.laundry.repo.CustomerRepository;
import com.laundry.repo.PaymentTransactionHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class InsightService {

    private final CustomerRepository customerRepo;
    private final PaymentTransactionHistory paymentRepo;
    private final CustomerAccountRepository accountRepo;

    public InsightResponse getInsights(){
        long totalCustomer=customerRepo.count();

        double totalRevenue=0.0;


        // get all the payments
        List<PaymentTransactions> allPayments=paymentRepo.findAll();
        for(PaymentTransactions payments:allPayments){
            totalRevenue+= payments.getAmount();
        }

        Map<Long,Double> paymentMap=new HashMap<>();

        for(PaymentTransactions payments:allPayments){
            Long customerId=payments.getAccount().getId();
            double amount=payments.getAmount();

            if (paymentMap.containsKey(customerId)){
                paymentMap.put(customerId, paymentMap.get(customerId)+amount);
            }else {
                paymentMap.put(customerId,amount);
            }
        }

        List<Map.Entry<Long, Double>> sortedEntries = new ArrayList<>(paymentMap.entrySet());
        sortedEntries.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        List<TopCustomers> topCustomers = new ArrayList<>();
        int count = 0;
        for (Map.Entry<Long, Double> entry : sortedEntries) {
            if (count >= 3) break;

            Long customerId = entry.getKey();
            Customer customer = customerRepo.findById(customerId).orElse(null);
            if (customer != null) {
                TopCustomers top = new TopCustomers(customerId, customer.getName(), entry.getValue());
                topCustomers.add(top);
                count++;
            }
        }

        List<DueCustomers> dueCustomers=new ArrayList<>();
        List<CustomerAccount> accounts=accountRepo.findAll();

        for (CustomerAccount account:accounts){
            if (account.getBalance() > 0){
                Customer customer=account.getCustomer();
                DueCustomers due=new DueCustomers(customer.getId(),customer.getName(),account.getBalance());
                dueCustomers.add(due);
            }
        }

        return new InsightResponse(totalCustomer,totalRevenue,topCustomers,dueCustomers);
    }
}
