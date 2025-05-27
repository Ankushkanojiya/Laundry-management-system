package com.laundry.service;


import com.laundry.dto.OrderRequest;
import com.laundry.dto.OrderResponse;
import com.laundry.model.Customer;
import com.laundry.model.Order;
import com.laundry.repo.CustomerRepository;
import com.laundry.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
//    private static final double PRICE_PER_CLOTH=10.00;
//
//    private static final double LAUNDRY_PRICE=40.00;

    public OrderResponse createOrder(OrderRequest request){
        Customer customer = customerRepo.findById(request.getCustomerId())
                .orElseThrow(()-> new RuntimeException(" no customer found"));

        double pricePerCloth = switch (request.getServiceType()) {
            case "LAUNDRY" -> 50.00;
            default -> 10.00; // Regular wash
        };

        Order order= Order.builder()
                .customer(customer)
                .orderDate(request.getOrderDate())
                .totalClothes(request.getTotalClothes())
                .totalAmount(request.getTotalClothes() * pricePerCloth)
                .serviceType(Order.ServiceType.valueOf(request.getServiceType()))
                .status(Order.OrderStatus.PENDING)
                .build();

        Order savedOrder=orderRepo.save(order);
        return mapToResponse(savedOrder);

    }

    public  OrderResponse updateStatus(Long orderId, String newStatus){
        Order order= orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order Id is not found"));

        try {
            Order.OrderStatus status= Order.OrderStatus.valueOf(newStatus);
            order.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(" Invalid Status");
        }

        return mapToResponse(orderRepo.save(order));
    }

    public List<OrderResponse> getAllOrders(){
        List<Order> orders= orderRepo.findAllByOrderByOrderDateDesc();
        List<OrderResponse> responses= new ArrayList<>();

        for(Order order:orders){
            responses.add(OrderResponse.builder()
                    .id(order.getId())
                    .customerName(order.getCustomer().getName())
                    .customerPhone(order.getCustomer().getPhoneNumber())
                    .totalClothes(order.getTotalClothes())
                    .totalAmount(order.getTotalAmount())
                    .orderDate(order.getOrderDate())
                    .serviceType(order.getServiceType().name())
                    .status(order.getStatus().name())
                    .build());
        }
        return responses;
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomer().getName())
                .customerPhone(order.getCustomer().getPhoneNumber())
                .orderDate(order.getOrderDate())
                .totalClothes(order.getTotalClothes())
                .totalAmount(order.getTotalAmount())
                .serviceType(order.getServiceType().name())
                .status(order.getStatus().name())
                .build();
    }

    public List<OrderResponse> getFilteredOrders(Order.OrderStatus status, Long customerId, LocalDate startDate, LocalDate endDate){
        List<Order> orders=orderRepo.findFilteredOrder(status,customerId,startDate,endDate);
        List<OrderResponse> responses=new ArrayList<>();

        for (Order order:orders){
            responses.add(OrderResponse.builder()
                    .id(order.getId())
                    .customerName(order.getCustomer().getName())
                    .customerPhone(order.getCustomer().getPhoneNumber())
                    .totalClothes(order.getTotalClothes())
                    .totalAmount(order.getTotalAmount())
                    .orderDate(order.getOrderDate())
                    .serviceType(order.getServiceType().name())
                    .status(order.getStatus().name())
                    .build());
        }
        return responses;
    }



    // this is redundant method as this functionality can be achieved by getFilteredOrders()
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        List<Order> orders=orderRepo.findByCustomerId(customerId);
        List<OrderResponse> responses = new ArrayList<>();

        for(Order order:orders){
            responses.add(OrderResponse.builder()
                    .id(order.getId())
                    .customerName(order.getCustomer().getName())
                    .customerPhone(order.getCustomer().getPhoneNumber())
                    .totalClothes(order.getTotalClothes())
                    .totalAmount(order.getTotalAmount())
                    .orderDate(order.getOrderDate())
                    .status(order.getStatus().name())
                    .build());
        }
        return responses;
    }

    // this is redundant method as this functionality can be achieved by getFilteredOrders()
    public List<OrderResponse> getOrdersByStatus(String status){
        List<Order> orders=orderRepo.findByStatus(Order.OrderStatus.valueOf(status));
        List<OrderResponse> responses=new ArrayList<>();
            for(Order order:orders){
                responses.add(OrderResponse.builder()
                        .id(order.getId())
                        .customerName(order.getCustomer().getName())
                        .customerPhone(order.getCustomer().getPhoneNumber())
                        .totalClothes(order.getTotalClothes())
                        .totalAmount(order.getTotalAmount())
                        .serviceType(order.getServiceType().name())
                        .status(order.getStatus().name())
                        .build());
            }
            return responses;
    }
}
