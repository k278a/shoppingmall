package com.personal.shoppingmall.product.repository;

import com.personal.shoppingmall.product.entity.Product;
import com.personal.shoppingmall.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductNameAndSeller(String productName, Seller seller);
}
