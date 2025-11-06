package com.laundry.controller;

import com.laundry.dto.*;
import com.laundry.service.CustomerAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/customer-auth")
@RequiredArgsConstructor
@RestController
public class CustomerAuthController {

    private final CustomerAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register( @Valid @RequestBody CustomerRegisterRequest request){
        String msg=authService.register(request);
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody CustomerLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PatchMapping("/me/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request, @RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(authService.changePassword(request,authHeader));
    }

}
