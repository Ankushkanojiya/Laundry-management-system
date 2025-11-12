package com.laundry.repo;


import com.laundry.model.Customer;
import com.laundry.model.Otp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,String> {


    Otp findByCustomerIdAndOtpCode(Long customerId, String otpCode);
    Otp findByCustomerId(Long customerId);
}
