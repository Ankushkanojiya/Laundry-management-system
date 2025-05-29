package com.laundry.service;

import com.laundry.dto.CustomerRequest;
import com.laundry.dto.CustomerResponse;
import com.laundry.model.Customer;
import com.laundry.repo.CustomerRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {


    private final CustomerRepository customerRepo;



    public CustomerResponse addCustomer(CustomerRequest request){
        // Add phone number validation
        if (customerRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Phone number already exists"
            );
        }
            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setPhoneNumber(request.getPhoneNumber());

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
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setName(request.getName());
        customer.setPhoneNumber(request.getPhoneNumber());

        return mapToResponse(customerRepo.save(customer));
    }

    public void deleteCustomer(Long id){
        customerRepo.deleteById(id);
    }

}
