package com.laundry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(regexp = "^\\d{10}$",message = "The number should be 10 digits")
    private String phoneNumber;

}
