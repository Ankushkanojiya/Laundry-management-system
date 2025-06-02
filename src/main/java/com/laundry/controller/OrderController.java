package com.laundry.controller;

import com.laundry.dto.OrderRequest;
import com.laundry.dto.OrderResponse;
import com.laundry.model.Order;
import com.laundry.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid  @RequestBody OrderRequest request) {
        if (request.getTotalClothes() == null || request.getTotalClothes() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }


    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long orderId, @RequestParam String newStatus) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, newStatus));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable String status){
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<OrderResponse>> getFilteredOrders(
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                orderService.getFilteredOrders(status, customerId, startDate, endDate)
        );
    }
}

