package com.personal.shoppingmall.wishlist.service;

import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.product.entity.Product;
import com.personal.shoppingmall.product.repository.ProductRepository;
import com.personal.shoppingmall.user.entity.User;
import com.personal.shoppingmall.user.repository.UserRepository;
import com.personal.shoppingmall.wishlist.dto.WishListItemRequestDto;
import com.personal.shoppingmall.wishlist.dto.WishListItemResponseDto;
import com.personal.shoppingmall.wishlist.entity.WishList;
import com.personal.shoppingmall.wishlist.entity.WishListItem;
import com.personal.shoppingmall.wishlist.repository.WishListItemRepository;
import com.personal.shoppingmall.wishlist.repository.WishListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishListService {

    private final WishListRepository wishListRepository;
    private final WishListItemRepository wishListItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishListService(WishListRepository wishListRepository,
                           WishListItemRepository wishListItemRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.wishListRepository = wishListRepository;
        this.wishListItemRepository = wishListItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WishListItemResponseDto addToWishList(String email, WishListItemRequestDto requestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND, "User not found with email: " + email));

        WishList wishList = wishListRepository.findByUser(user)
                .orElseGet(() -> wishListRepository.save(new WishList(user)));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCodes.PRODUCT_NOT_FOUND, "Product not found with ID: " + requestDto.getProductId()));

        WishListItem wishListItem = new WishListItem(
                product.getProductName(),
                product.getProductDescription(),
                product.getPrice(),
                requestDto.getQuantity(),
                wishList
        );

        // Directly save without try-catch
        wishListItem = wishListItemRepository.save(wishListItem);

        return new WishListItemResponseDto(
                wishListItem.getId(),
                wishListItem.getProductName(),
                wishListItem.getProductDescription(),
                wishListItem.getPrice(),
                wishListItem.getQuantity()
        );
    }
}
