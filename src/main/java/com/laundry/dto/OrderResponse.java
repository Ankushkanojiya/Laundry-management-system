package com.laundry.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private String customerName;
    private String customerPhone;
    private LocalDate orderDate;
    private Integer totalClothes;
    private Double totalAmount;
    private String serviceType;
    private String status;


}

