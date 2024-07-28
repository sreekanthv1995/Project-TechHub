package com.tech_hub.techhub.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddressDto {

    private Integer id;
    private String houseNumberOrName;
    private String area;
    private String city;
    private String state;
    private String pinCode;
    private String landmark;
    private LocalDate createdAt;
    private Long userId;
}
