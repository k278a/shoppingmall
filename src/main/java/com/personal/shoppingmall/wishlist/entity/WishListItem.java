package com.personal.shoppingmall.wishlist.entity;

import com.personal.shoppingmall.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "wishlist_item")
public class WishListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String productDescription;
    private Long price;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private WishList wishList;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Product와의 관계를 추가합니다.

    public WishListItem(String productName, String productDescription, Long price, int quantity, WishList wishList, Product product) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.price = price;
        this.quantity = quantity;
        this.wishList = wishList;
        this.product = product; // Product 필드를 초기화합니다.
    }

    public void updateQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        } else {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    public Long getProductId() {
        return product.getId(); // Product의 ID를 반환합니다.
    }
}
