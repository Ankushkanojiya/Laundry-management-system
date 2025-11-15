package com.laundry.service;

import com.laundry.dto.*;
import com.laundry.exception.*;
import com.laundry.model.Customer;
import com.laundry.model.CustomerLogin;
import com.laundry.model.Otp;
import com.laundry.repo.CustomerLoginRepository;
import com.laundry.repo.CustomerRepository;
import com.laundry.repo.OtpRepository;
import com.laundry.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomerAuthService {
    private final CustomerLoginRepository loginRepo;
    private final CustomerRepository customerRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SecureRandom random=new SecureRandom();
    private final OtpRepository otpRepo;
    private final MailService mail;

    public String register(CustomerRegisterRequest request){
        String phone=request.getPhoneNumber();
        String email=request.getEmail();

        Customer customer=customerRepo.findByPhoneNumber(phone)
                .orElseThrow(() ->new RuntimeException("Please ask your admin to register your number"));

        if (loginRepo.existsByPhoneNumber(phone)){
            throw new PhoneNumberAlreadyExistException(phone);
        }

        if (customerRepo.existsByEmail(email)){
            throw new RuntimeException("Email already registered");
        }

        customer.setEmail(email);
        customerRepo.save(customer);

        CustomerLogin customerLogin=new CustomerLogin();
        customerLogin.setPhoneNumber(phone);
        customerLogin.setPassword(passwordEncoder.encode(request.getPassword()));
        customerLogin.setCustomer(customer);
        loginRepo.save(customerLogin);

        return "registration Successful";
    }

    public JwtResponse login(CustomerLoginRequest request){
        Customer customer=customerRepo.findByPhoneNumber(request.getPhoneNumber()).orElseThrow(()-> new RuntimeException("Invalid phone number"));

        CustomerLogin login=loginRepo.findByCustomer(customer).orElseThrow(CustomerLoginNotFoundException::new);

        if(!login.isActive()){
            throw new CustomerLoginNotFoundException();
        }
        if (!passwordEncoder.matches(request.getPassword(), login.getPassword())){
            throw new InvalidPhoneAndPassword();
        }
        String token=jwtUtil.generateToken(login.getPhoneNumber());
        System.out.println("The customer id is "+ customer.getId());

        return new JwtResponse(token, customer.getId(), customer.getName());

    }

    public String changePassword(PasswordChangeRequest request, String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new MissingTokenException();
        }

        String token=authHeader.substring(7);
        String phone= jwtUtil.extractPhone(token);

        Customer customer=customerRepo.findByPhoneNumber(phone).orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerLogin login=loginRepo.findByCustomer(customer).orElseThrow(() -> new RuntimeException("Customer Account is not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), login.getPassword())){
            throw new IncorrectCurrentPasswordException();
        }
        login.setPassword(passwordEncoder.encode(request.getNewPassword()));
        loginRepo.save(login);

        return "Successfully change password";
    }

    public Map<String, String> sendOtpToEmail(ForgotPasswordRequest request) {
         String email=request.getEmail();
         Customer customer=customerRepo.findByEmail(email);

         if(customer == null){
             throw new CustomerNotFoundException(0L);
         }

         Long customerId=customer.getId();
         Otp otp=generateOtp(customerId);

         otpRepo.save(otp);

         mail.sendMail(email,"Verification OTP - Laundry Management System",otp.getOtpCode());
         return Map.of("success", "OTP has been sent to your Email");
    }
    private Otp generateOtp(Long customerId){
        Otp existingOtp = otpRepo.findByCustomerId(customerId);
        if (existingOtp != null) {
            otpRepo.delete(existingOtp);
        }
        int otpDigit=100000+ random.nextInt(900000);
        String otpCode=String.valueOf(otpDigit);
        Otp otp=new Otp();
        otp.setId(UUID.randomUUID().toString());
        otp.setCustomerId(customerId);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        return otp;
    }

    public void verifyOtpAndSetPassword(VerifyOtpRequest request){

        Customer customer = customerRepo.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new CustomerNotFoundException(0L));

        System.out.println("The OTP etails"+customer.getName()+customer.getEmail()+customer.getPhoneNumber());
        Otp otp = otpRepo.findByCustomerIdAndOtpCode(customer.getId(), request.getOtpCode());
        if (otp == null) {
            throw new RuntimeException("Invalid or incorrect OTP code.");
        }
        System.out.println("The OTP details"+customer.getName()+customer.getEmail()+customer.getPhoneNumber());

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepo.delete(otp);
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }
        CustomerLogin customerLogin=loginRepo.findByCustomer(customer)
                .orElseThrow(CustomerLoginNotFoundException::new);

        customerLogin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        loginRepo.save(customerLogin);
        otpRepo.delete(otp);

    }
}
