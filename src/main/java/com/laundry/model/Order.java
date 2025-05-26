package com.laundry.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "laundry_order") // Avoid SQL keyword conflict
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)

    private LocalDate orderDate;

    @Column(nullable = false)
    private Integer totalClothes;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType=ServiceType.REGULAR;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public enum OrderStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }

    public enum ServiceType{
        REGULAR, LAUNDRY
    }
}
