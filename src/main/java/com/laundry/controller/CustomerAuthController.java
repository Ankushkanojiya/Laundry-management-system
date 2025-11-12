package com.laundry.controller;

import com.laundry.dto.*;
import com.laundry.service.CustomerAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String,String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest otp){
        authService.sendOtpToEmail(otp);
        return ResponseEntity.ok(Map.of("message", "an OTP has been sent."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String,String>> verifyOtpAndSetPassword(@RequestBody @Valid VerifyOtpRequest verifyOtpRequest){
        authService.verifyOtpAndSetPassword(verifyOtpRequest);
        return ResponseEntity.ok(Map.of("message","Password change is successfull!!"));
    }
}
