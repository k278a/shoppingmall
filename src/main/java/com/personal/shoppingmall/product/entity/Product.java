package com.personal.shoppingmall.product.entity;

import com.personal.shoppingmall.seller.entity.Seller;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Product {

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

    // 생성자 (필수 필드만 포함)
    public Product(String productName, String productDescription, int productStock, Long price, String categoryname, Seller seller) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productStock = productStock;
        this.price = price;
        this.categoryname = categoryname;
        this.seller = seller;
    }

    // 모든 필드를 포함한 생성자 (테스트나 다른 용도에서 사용)
    public Product(Long id, String productName, String productDescription, int productStock, Long price, String categoryname, Seller seller) {
        this.id = id;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productStock = productStock;
        this.price = price;
        this.categoryname = categoryname;
        this.seller = seller;
    }

    // 업데이트 메서드
    public void updateDetails(String productName, String productDescription, int productStock, Long price, String categoryname) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productStock = productStock;
        this.price = price;
        this.categoryname = categoryname;
    }
}
