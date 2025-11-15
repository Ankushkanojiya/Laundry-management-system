package com.laundry.service;

import com.laundry.dto.CustomerRequest;
import com.laundry.dto.CustomerResponse;
import com.laundry.exception.*;
import com.laundry.model.*;
import com.laundry.repo.*;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {


    private final CustomerRepository customerRepo;
    private final CustomerAccountRepository accountRepo;
    private final OrderRepository orderRepo;
    private final PendingCustomerPaymentRepository pendingRepo;
    private final CustomerLoginRepository loginRepo;



    public CustomerResponse addCustomer(CustomerRequest request){
        // Add phone number validation
        if (customerRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistException(request.getPhoneNumber());
        }
        if (request.getEmail() !=null && !request.getEmail().isBlank()) {
            if (customerRepo.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
        }
            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setPhoneNumber(request.getPhoneNumber());
            customer.setEmail(request.getEmail());
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
        List<Customer> customers=customerRepo.findByIsActiveTrue();
        List<CustomerResponse> customerResponses=new ArrayList<>();

        for(Customer customer : customers){
            customerResponses.add(mapToResponse(customer));
        }
        return customerResponses;
    }

    public CustomerResponse getCustomerById(Long id) {
        return customerRepo.findByIdAndIsActiveTrue(id)
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

        if(orderRepo.existsByCustomerAndStatus(customer, Order.OrderStatus.PENDING)){
            throw new PendingOrderException();
        }

        if (pendingRepo.existsByAccountAndCustomerPaymentStatus(account, PendingCustomerPayment.PaymentStatus.PENDING)){
            throw new PendingPaymentExistsException();
        }

        customer.setActive(false);
        customerRepo.save(customer);

        Optional<CustomerLogin> login=loginRepo.findByCustomer(customer);
        login.ifPresent(customerLogin ->{
            customerLogin.setActive(false);
            loginRepo.save(customerLogin);

        });

        System.out.println("Customer: "+customer.getName()+ " has been deactivated");
    }

}
