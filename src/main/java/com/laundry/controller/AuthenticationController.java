package com.laundry.controller;

import com.laundry.dto.LoginRequest;
import com.laundry.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.laundry.repo.AdminRepository;

//@RequestMapping("/api/login")
@RestController
public class AuthenticationController {

    @Autowired
    private AdminRepository adminRepo;

//    @PostMapping
//    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
//        Admin admin = adminRepo.findByUsername(request.getUsername());
//        if (admin != null && admin.getPassword().equals(request.getPassword())) {
//            return ResponseEntity.ok("success");
//        } else {
//            return ResponseEntity.status(401).body("error: Invalid credentials");
//        }
//    }
}
