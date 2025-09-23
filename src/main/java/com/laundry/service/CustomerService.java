package com.laundry.service;

import com.laundry.dto.CustomerRequest;
import com.laundry.dto.CustomerResponse;
import com.laundry.exception.BalanceNotSettledException;
import com.laundry.exception.CustomerNotFoundException;
import com.laundry.exception.PhoneNumberAlreadyExistException;
import com.laundry.model.Customer;
import com.laundry.model.CustomerAccount;
import com.laundry.repo.CustomerAccountRepository;
import com.laundry.repo.CustomerRepository;
import com.laundry.repo.OrderRepository;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {


    private final CustomerRepository customerRepo;
    private final CustomerAccountRepository accountRepo;
    private final OrderRepository orderRepo;



    public CustomerResponse addCustomer(CustomerRequest request){
        // Add phone number validation
        if (customerRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistException(request.getPhoneNumber());
        }
            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setPhoneNumber(request.getPhoneNumber());
            customerRepo.save(customer);

            CustomerAccount newAccount = new CustomerAccount();
            newAccount.setCustomer(customer);
            newAccount.setBalance(0.0);
            accountRepo.save(newAccount);

            Customer saveCustomer = customerRepo.save(customer);
            return mapToResponse(saveCustomer);

    }

    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response=new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setRegistrationDate(customer.getRegistrationDate());
        return response;
    }

    public List<CustomerResponse> getAllCustomers(){
        List<Customer> customers=customerRepo.findAll();
        List<CustomerResponse> customerResponses=new ArrayList<>();

        for(Customer customer : customers){
            customerResponses.add(mapToResponse(customer));
        }
        return customerResponses;
    }

    public CustomerResponse getCustomerById(Long id) {
        return customerRepo.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setName(request.getName());
        customer.setPhoneNumber(request.getPhoneNumber());

        return mapToResponse(customerRepo.save(customer));
    }

    @Transactional
    public void deleteCustomer(Long id){
        Customer customer=customerRepo.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        System.out.println("Bring it on");

        CustomerAccount account = accountRepo.findByCustomer(customer)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if(account.getBalance() !=0.0) throw new BalanceNotSettledException(account.getId(), account.getBalance());

        orderRepo.deleteByCustomer(customer);
        System.out.println("Deleted the orders");
        accountRepo.deleteByCustomer(customer);
        System.out.println("Deleted the account");
        customerRepo.deleteById(id);
        System.out.println("finally customer deleted");
    }

}
