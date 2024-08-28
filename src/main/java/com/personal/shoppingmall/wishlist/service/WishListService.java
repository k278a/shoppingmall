package com.personal.shoppingmall.wishlist.service;

import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.order.dto.OrderDetailRequestDto;
import com.personal.shoppingmall.order.dto.OrderRequestDto;
import com.personal.shoppingmall.order.dto.OrderResponseDto;
import com.personal.shoppingmall.order.service.OrderService;
import com.personal.shoppingmall.product.entity.Product;
import com.personal.shoppingmall.product.repository.ProductRepository;
import com.personal.shoppingmall.product.service.ProductService;
import com.personal.shoppingmall.user.entity.User;
import com.personal.shoppingmall.user.repository.UserRepository;
import com.personal.shoppingmall.wishlist.dto.*;
import com.personal.shoppingmall.wishlist.entity.WishList;
import com.personal.shoppingmall.wishlist.entity.WishListItem;
import com.personal.shoppingmall.wishlist.repository.WishListItemRepository;
import com.personal.shoppingmall.wishlist.repository.WishListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class WishListService {

    private final WishListRepository wishListRepository;
    private final WishListItemRepository wishListItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final ProductService productService;

    public WishListService(WishListRepository wishListRepository,
                           WishListItemRepository wishListItemRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           OrderService orderService,
                           ProductService productService) {
        this.wishListRepository = wishListRepository;
        this.wishListItemRepository = wishListItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
        this.productService = productService;
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
                wishList,
                product
        );

        wishListItem = wishListItemRepository.save(wishListItem);

        return new WishListItemResponseDto(
                wishListItem.getId(),
                wishListItem.getProductName(),
                wishListItem.getProductDescription(),
                wishListItem.getPrice(),
                wishListItem.getQuantity()
        );
    }

    @Transactional
    public WishListItemResponseDto updateQuantity(String email, Long itemId, int quantity) {
        if (quantity < 0) {
            throw new CustomException(ErrorCodes.INVALID_QUANTITY, "Quantity cannot be negative");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND, "User not found with email: " + email));

        WishList wishList = wishListRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCodes.WISHLIST_NOT_FOUND, "Wishlist not found for user: " + email));

        WishListItem wishListItem = wishListItemRepository.findById(itemId)
                .filter(item -> item.getWishList().equals(wishList))
                .orElseThrow(() -> new CustomException(ErrorCodes.WISHLIST_ITEM_NOT_FOUND, "Wishlist item not found with ID: " + itemId));

        wishListItem.updateQuantity(quantity);
        wishListItem = wishListItemRepository.save(wishListItem);

        return new WishListItemResponseDto(
                wishListItem.getId(),
                wishListItem.getProductName(),
                wishListItem.getProductDescription(),
                wishListItem.getPrice(),
                wishListItem.getQuantity()
        );
    }

    @Transactional
    public void deleteWishListItem(String email, Long itemId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND, "User not found with email: " + email));

        WishList wishList = wishListRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCodes.WISHLIST_NOT_FOUND, "Wishlist not found for user: " + email));

        WishListItem wishListItem = wishListItemRepository.findById(itemId)
                .filter(item -> item.getWishList().equals(wishList))
                .orElseThrow(() -> new CustomException(ErrorCodes.WISHLIST_ITEM_NOT_FOUND, "Wishlist item not found with ID: " + itemId));

        wishListItemRepository.delete(wishListItem);
    }



    @Transactional
    public WishListOrderResponseDto orderWishListItem(String email, Long itemId) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND, "User not found with email: " + email));

        // 위시리스트 조회
        WishList wishList = wishListRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCodes.WISHLIST_NOT_FOUND, "Wishlist not found for user: " + email));

        // 위시리스트 아이템 조회
        WishListItem wishListItem = wishListItemRepository.findById(itemId)
                .filter(item -> item.getWishList().equals(wishList))
                .orElseThrow(() -> new CustomException(ErrorCodes.WISHLIST_ITEM_NOT_FOUND, "Wishlist item not found with ID: " + itemId));

        // 수량을 단순히 가져오기
        int quantity = wishListItem.getQuantity();

        // 상품 재고 확인 및 차감
        productService.reduceProductStock(wishListItem.getProductId(), quantity);

        // 주문 요청 DTO 생성
        OrderRequestDto orderRequestDto = new OrderRequestDto(
                List.of(new OrderDetailRequestDto(
                        wishListItem.getProductId(),
                        quantity
                ))
        );

        // 주문 생성
        OrderResponseDto orderResponseDto = orderService.createOrder(orderRequestDto);

        // 주문 후 위시리스트에서 아이템 삭제
        wishListItemRepository.delete(wishListItem);

        // 주문 응답 DTO 생성
        return new WishListOrderResponseDto(
                orderResponseDto.getId(),                  // 주문 ID
                orderResponseDto.getOrderStatus(),         // 주문 상태
                orderResponseDto.getTotalPrice(),          // 총 가격
                orderResponseDto.getCreatedAt(),           // 주문 생성 시간
                orderResponseDto.getUpdatedAt(),           // 주문 업데이트 시간
                List.of(new WishListOrderRequestDto(       // 주문 세부 사항 리스트
                        itemId,
                        quantity
                ))
        );
    }


}


