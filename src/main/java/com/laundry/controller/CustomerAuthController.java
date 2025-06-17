package com.laundry.controller;

import com.laundry.dto.CustomerLoginRequest;
import com.laundry.dto.CustomerLoginResponse;
import com.laundry.dto.CustomerRegisterRequest;
import com.laundry.service.CustomerAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/customer-auth")
@RequiredArgsConstructor
@RestController
public class CustomerAuthController {

    private final CustomerAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CustomerRegisterRequest request){
        String msg=authService.register(request);
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerLoginResponse> login(@RequestBody CustomerLoginRequest request){
        CustomerLoginResponse response=authService.login(request);
        return ResponseEntity.ok(response);
    }
}
