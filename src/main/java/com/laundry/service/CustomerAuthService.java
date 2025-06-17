package com.laundry.service;

import com.laundry.dto.CustomerLoginRequest;
import com.laundry.dto.CustomerLoginResponse;
import com.laundry.dto.CustomerRegisterRequest;
import com.laundry.model.Customer;
import com.laundry.model.CustomerLogin;
import com.laundry.repo.CustomerLoginRepository;
import com.laundry.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerAuthService {
    private final CustomerLoginRepository loginRepo;
    private final CustomerRepository customerRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public String register(CustomerRegisterRequest request){
        String phone=request.getPhoneNumber();

        if (!customerRepo.existsByPhoneNumber(phone)){
            throw new RuntimeException("Please ask your admin to register your number");
        }

        if (loginRepo.existsByPhoneNumber(phone)){
            throw new RuntimeException("Already register, please Login");
        }

        Customer customer=customerRepo.findAll()
                .stream()
                .filter(c -> c.getPhoneNumber().equals(phone))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Customer not found"));

        CustomerLogin customerLogin=new CustomerLogin();
        customerLogin.setPhoneNumber(phone);
        customerLogin.setPassword(passwordEncoder.encode(request.getPassword()));
        customerLogin.setCustomer(customer);
        loginRepo.save(customerLogin);

        return "registration Successful";
    }

    public CustomerLoginResponse login(CustomerLoginRequest request){
        CustomerLogin login=loginRepo.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Invalid phone number or password"));

        if (!passwordEncoder.matches(request.getPassword(),login.getPassword())){
            throw new RuntimeException("Invalid phone or password");
        }

        return new CustomerLoginResponse("Login Successful");
    }
}
