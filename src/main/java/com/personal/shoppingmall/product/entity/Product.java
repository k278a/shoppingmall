package com.personal.shoppingmall.product.entity;


import com.personal.shoppingmall.seller.entity.Seller;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Product {

    // Getter 메서드들
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String productDescription;
    private int productStock;
    private Long price;
    private String categoryname;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    // 기본 생성자
    public Product() {}

    // 생성자
    public Product(String productName, String productDescription, int productStock, Long price, String categoryname, Seller seller) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productStock = productStock;
        this.price = price;
        this.categoryname = categoryname;
        this.seller = seller;
    }

}
