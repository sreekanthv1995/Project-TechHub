package com.tech_hub.techhub.entity;


import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "variant_id")
    private Variant variant;
    private Integer quantity;
    private double variantPrice;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
