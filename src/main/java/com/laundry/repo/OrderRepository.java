package com.laundry.repo;

import com.laundry.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);

//    Order findById();


    List<Order> findAllByOrderByOrderDateDesc();

//    List<Order> findByStatus(Order.OrderStatus status);


//    List<Order> findByStatus(OrderStatus status); // For filtering
//    long countByStatus(OrderStatus status); // For dashboard stats
//List<Order> findByStatus(Order.OrderStatus status);


    // For reports
//    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
//    List<Order> findOrdersBetweenDates(@Param("start") LocalDate start,
//                                       @Param("end") LocalDate end);
}
