package com.laundry.controller;

import com.laundry.dto.AdminAuthResponse;
import com.laundry.dto.AdminLoginRequest;
import com.laundry.model.Admin;
import com.laundry.service.AdminAuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/admin/auth")
@RestController
@AllArgsConstructor
public class AdminAuthController {
    private final AdminAuthService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request){
        AdminAuthResponse response=adminService.authenticate(request.getUsername(), request.getPassword());

        if (response == null){
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        return ResponseEntity.ok(response);
    }

}
