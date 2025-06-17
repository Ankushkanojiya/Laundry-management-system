package com.laundry.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.laundry.model.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull
    private LocalDate orderDate=LocalDate.now();

    @Min(value = 1, message = "At least 1 cloth required")
    private Integer totalClothes;

    @NotNull
    private String serviceType;

}
