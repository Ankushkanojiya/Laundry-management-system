package com.laundry.repo;  // Must match your package structure

import com.laundry.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    // This magic method finds admin by username
    Admin findByUsername(String username);
}