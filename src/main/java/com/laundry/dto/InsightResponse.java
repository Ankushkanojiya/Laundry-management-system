package com.laundry.dto;

import com.laundry.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsightResponse {
    private Long totalCustomers;
    private Long totalOrders;
    private double totalRevenue;
    private List<TopCustomers> topCustomers;
    private List<DueCustomers> customersWithDue;
}
