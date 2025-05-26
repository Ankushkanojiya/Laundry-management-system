package com.laundry.controller;

import com.laundry.dto.CustomerRequest;
import com.laundry.dto.CustomerResponse;
import com.laundry.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    public ResponseEntity<CustomerResponse> addCustomer(@Valid @RequestBody CustomerRequest request) {
        System.out.println("CustomerService instance: " + customerService); // Debug line
        CustomerResponse response = customerService.addCustomer(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }


    // Get single customer
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // Update customer
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request
    ) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomers(@PathVariable Long id){
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Deleted Successfully");
    }

//    @GetMapping("/search")
//    public ResponseEntity<CustomerResponse> getByPhone(@RequestParam String phone) {
//        return ResponseEntity.ok(customerService.findByPhoneNumber(phone));
//    }

}
