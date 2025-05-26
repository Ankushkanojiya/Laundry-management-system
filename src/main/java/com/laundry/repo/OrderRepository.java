package com.laundry.repo;

import com.laundry.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);

    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findByStatus(Order.OrderStatus status);

    @Query("Select COUNT(o) from Order o where o.status='PENDING' ")
    long countByStatus(Order.OrderStatus status);

    @Query("Select COALESCE(sum(o.totalAmount),0) from Order o where o.orderDate= :date")
    double sumOfRevenueToday(@Param("date")LocalDate date);
//    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
//    List<Order> findOrdersBetweenDates(@Param("start") LocalDate start,
//                                       @Param("end") LocalDate end);
}
