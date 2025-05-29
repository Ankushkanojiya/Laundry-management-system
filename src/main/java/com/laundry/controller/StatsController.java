package com.laundry.controller;


import com.laundry.model.Order;
import com.laundry.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;


@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    @Autowired
    private  OrderRepository orderRepo;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats(){
        long pendingOrders= orderRepo.countByStatus(Order.OrderStatus.PENDING);
        double revenueToday=orderRepo.sumOfRevenueToday(LocalDate.now());
        double businessRevenueToday=orderRepo.sumOfBusinessRevenueToday(LocalDate.now());

        return ResponseEntity.ok(Map.of(
                "pendingOrders",pendingOrders,
                "businessRevenueToday",businessRevenueToday,
                "revenueToday",revenueToday

        ));
    }
}
