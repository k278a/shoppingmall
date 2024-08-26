package com.personal.shoppingmall.order.entity;

import com.personal.shoppingmall.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Long price;
    private int orderNumber;

    public OrderDetail() {
    }

    public OrderDetail(Order order, Product product, Long price, int orderNumber) {
        this.order = order;
        this.product = product;
        this.price = price;
        this.orderNumber = orderNumber;
    }

}
