package com.laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private  Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private LocalDate registrationDate;

}
