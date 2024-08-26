package com.personal.shoppingmall.product.controller;


import com.personal.shoppingmall.product.dto.ProductRequestDto;
import com.personal.shoppingmall.product.dto.ProductResponseDto;
import com.personal.shoppingmall.product.service.ProductService;
import com.personal.shoppingmall.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;

    public ProductController(ProductService productService, JwtTokenProvider jwtTokenProvider) {
        this.productService = productService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 셀러 상품 생성
    @PostMapping("/seller")
    public ResponseEntity<ProductResponseDto> createSellerProduct(
            @RequestHeader("Authorization") String token,
            @RequestBody ProductRequestDto productRequestDto) {
        String sellerEmail = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        ProductResponseDto createdProduct = productService.createSellerProduct(sellerEmail, productRequestDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

}
