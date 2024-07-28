package com.tech_hub.techhub.entity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String houseNumberOrName;
    private String area;
    private String city;
    private String state;
    private String pinCode;
    private String landmark;
    private boolean isDelete;


    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    private LocalDate createdAt;
}
