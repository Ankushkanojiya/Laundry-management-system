package com.laundry.service;

import com.laundry.dto.AdminAuthResponse;
import com.laundry.model.Admin;
import com.laundry.repo.AdminRepository;
import com.laundry.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AdminAuthResponse authenticate(String username, String rawPassword){
        Admin admin=adminRepo.findByUsername(username);
        if (admin==null || !passwordEncoder.matches(rawPassword, admin.getPassword())) return null;

        String token=jwtUtil.generateToken(username);
        return new AdminAuthResponse(token);




    }
}
