package com.laundry.service;

import com.laundry.dto.*;
import com.laundry.model.Customer;
import com.laundry.model.CustomerLogin;
import com.laundry.repo.CustomerLoginRepository;
import com.laundry.repo.CustomerRepository;
import com.laundry.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomerAuthService {
    private final CustomerLoginRepository loginRepo;
    private final CustomerRepository customerRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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

    public JwtResponse login(CustomerLoginRequest request){
        Customer customer=customerRepo.findByPhoneNumber(request.getPhoneNumber()).orElseThrow(()-> new RuntimeException("Invalid phone number"));

        CustomerLogin login=loginRepo.findByCustomer(customer).orElseThrow(() -> new RuntimeException("No account found"));

        if (!passwordEncoder.matches(request.getPassword(), login.getPassword())){
            throw new RuntimeException("Invalid phone number and password");
        }
        String token=jwtUtil.generateToken(login.getPhoneNumber());
        System.out.println("The customer id is "+ customer.getId());

        return new JwtResponse(token, customer.getId(), customer.getName());

    }

    public String changePassword(PasswordChangeRequest request, String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new RuntimeException("Missing token");
        }

        String token=authHeader.substring(7);
        String phone= jwtUtil.extractPhone(token);

        Customer customer=customerRepo.findByPhoneNumber(phone).orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerLogin login=loginRepo.findByCustomer(customer).orElseThrow(() -> new RuntimeException("Customer Account is not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), login.getPassword())){
            throw new RuntimeException("Incorrect current password");
        }
        login.setPassword(passwordEncoder.encode(request.getNewPassword()));
        loginRepo.save(login);

        return "Successfully change password";
    }
}
