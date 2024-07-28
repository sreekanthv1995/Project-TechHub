package com.tech_hub.techhub.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private String name;

    private String description;

    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id",referencedColumnName = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product",cascade = CascadeType.PERSIST,orphanRemoval = true)
    @JsonIgnore
    private List<Variant> variant;

    @OneToMany(mappedBy = "products",cascade = CascadeType.ALL)
    private List<Image> images;


}
