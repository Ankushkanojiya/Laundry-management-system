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

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED' AND o.orderDate = :date")
    double sumOfRevenueToday(@Param("date")LocalDate date);

    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:customerId IS NULL OR o.customer.id = :customerId) AND " +
            "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
            "(:endDate IS NULL OR o.orderDate <= :endDate)")
    List<Order> findFilteredOrder(@Param("status") Order.OrderStatus status, @Param("customerId") Long customerId, @Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);
//
//    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
//    List<Order> findOrdersBetweenDates(@Param("start") LocalDate start,
//                                       @Param("end") LocalDate end);
}
