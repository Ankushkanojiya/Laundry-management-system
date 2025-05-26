package com.laundry.controller;

import com.laundry.dto.LoginRequest;
import com.laundry.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.laundry.repo.AdminRepository;


@RestController
public class AuthenticationController {

    @Autowired
    private AdminRepository adminRepo;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        Admin admin = adminRepo.findByUsername(request.getUsername());
        if (admin != null && admin.getPassword().equals(request.getPassword())) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(401).body("error: Invalid credentials");
        }
    }

    // AuthenticationController.java - Add logout placeholder
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout() {
//        return ResponseEntity.ok("Logged out successfully");
//    }

}
