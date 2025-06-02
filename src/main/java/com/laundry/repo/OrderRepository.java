package com.laundry.repo;

import com.laundry.dto.PaymentSummary;
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

    @Query("Select COALESCE(sum(o.totalAmount),0) from Order o where o.orderDate= :date")
    double sumOfBusinessRevenueToday(@Param("date") LocalDate date);

    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:customerId IS NULL OR o.customer.id = :customerId) AND " +
            "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
            "(:endDate IS NULL OR o.orderDate <= :endDate)")
    List<Order> findFilteredOrder(@Param("status") Order.OrderStatus status, @Param("customerId") Long customerId, @Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);
//

//    @Query("SELECT NEW com.laundry.dto.PaymentSummary(" +
//            "o.customer.id, o.customer.name, " +
//            "CAST(SUM(o.totalClothes) AS LONG), " + // Cast to Long
//            "SUM(o.totalAmount)) " +
//            "FROM Order o " +
//            "WHERE o.status IN ('PENDING', 'IN_PROGRESS') " +
//            "GROUP BY o.customer.id, o.customer.name")
//    List<PaymentSummary> getPaymentSummary();

}
