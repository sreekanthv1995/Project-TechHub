package com.tech_hub.techhub.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderCsvDto {

    private String username;
    private String orderId;
    private LocalDate orderDate;
    private String status;
    private Double totalPrice;
    private String paymentMode;
    private String productName;
    private Double totalSales;
    private int totalOrders;
}
