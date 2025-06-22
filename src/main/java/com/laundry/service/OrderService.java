package com.laundry.service;


import com.laundry.dto.OrderRequest;
import com.laundry.dto.OrderResponse;
import com.laundry.dto.PaymentSummary;
import com.laundry.model.Customer;
import com.laundry.model.CustomerAccount;
import com.laundry.model.Order;
import com.laundry.repo.CustomerAccountRepository;
import com.laundry.repo.CustomerRepository;
import com.laundry.repo.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final double REGULAR_PRICE_PER_ITEM = 10.0;
    private static final double LAUNDRY_PRICE_PER_ITEM = 50.0;

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final CustomerAccountRepository accountRepo;





    public OrderResponse createOrder(OrderRequest request){
        Customer customer = customerRepo.findById(request.getCustomerId())
                .orElseThrow(()-> new RuntimeException(" no customer found"));

        double totalAmount = calculateTotal(
                request.getTotalClothes(),
                Order.ServiceType.valueOf(request.getServiceType())
        );
        double pricePerCloth = switch (request.getServiceType()) {
            case "LAUNDRY" -> 50.00;
            default -> 10.00; // Regular wash
        };


        CustomerAccount account=accountRepo.findByCustomer(customer).orElseThrow(()-> new RuntimeException(" no customer found"));
        account.setBalance(account.getBalance() + totalAmount);
        accountRepo.save(account);

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

    private double calculateTotal(int totalClothes, Order.ServiceType serviceType) {
        double pricePerItem = serviceType == Order.ServiceType.LAUNDRY
                ? LAUNDRY_PRICE_PER_ITEM
                : REGULAR_PRICE_PER_ITEM;
        return totalClothes * pricePerItem;
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
                    .serviceType(order.getServiceType().name())
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

    @Transactional
    public void completeAllOrders(Customer customer) {
        List<Order> orders=orderRepo.findByCustomerAndStatusNot(customer, Order.OrderStatus.COMPLETED);

        orders.forEach(order -> {
            order.setStatus(Order.OrderStatus.COMPLETED);
        });
        orderRepo.saveAll(orders);
    }
}
