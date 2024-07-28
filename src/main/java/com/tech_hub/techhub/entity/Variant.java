package com.tech_hub.techhub.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "variant")
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    private Long id;

    private String variantName;
    private Float price;
    private Integer stock;
    private int returnedStock;
    private Float originalPrice;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Offer discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",referencedColumnName = "product_id")
    @JsonIgnore
    private Products product;

    @JsonProperty("variant_id")
    public Long getId() {
        return id;
    }
}
