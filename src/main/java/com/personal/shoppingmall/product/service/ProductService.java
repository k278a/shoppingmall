package com.personal.shoppingmall.product.service;

import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.product.dto.ProductRequestDto;
import com.personal.shoppingmall.product.dto.ProductResponseDto;
import com.personal.shoppingmall.product.entity.Product;
import com.personal.shoppingmall.product.repository.ProductRepository;
import com.personal.shoppingmall.seller.entity.Seller;
import com.personal.shoppingmall.seller.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    public ProductService(ProductRepository productRepository, SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
    }


    // 상품 생성 (셀러 연결)
    @Transactional
    public ProductResponseDto createSellerProduct(String sellerEmail, ProductRequestDto productRequestDto) {
        Seller seller = getSellerByEmail(sellerEmail);

        if (productRepository.existsByProductNameAndSeller(productRequestDto.getProductName(), seller)) {
            throw new CustomException(ErrorCodes.PRODUCT_ALREADY_EXISTS, "이미 존재하는 제품입니다.");
        }

        Product product = new Product(
                productRequestDto.getProductName(),
                productRequestDto.getProductDescription(),
                productRequestDto.getProductStock(),
                productRequestDto.getPrice(),
                productRequestDto.getCategoryname(),
                seller
        );

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    private Seller getSellerByEmail(String email) {
        return (Seller) sellerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCodes.SELLER_NOT_FOUND, "셀러를 찾을 수 없습니다."));
    }

    private ProductResponseDto convertToDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getProductName(),
                product.getProductDescription(),
                product.getProductStock(),
                product.getPrice(),
                product.getCategoryname()
        );
    }

}
