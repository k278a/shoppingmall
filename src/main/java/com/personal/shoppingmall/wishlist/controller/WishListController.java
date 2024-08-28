package com.personal.shoppingmall.wishlist.controller;

import com.personal.shoppingmall.wishlist.dto.WishListItemRequestDto;
import com.personal.shoppingmall.wishlist.dto.WishListItemResponseDto;
import com.personal.shoppingmall.wishlist.service.WishListService;
import com.personal.shoppingmall.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/wishlist")
public class WishListController {

    private final WishListService wishListService;
    private final JwtTokenProvider jwtTokenProvider;

    public WishListController(WishListService wishListService, JwtTokenProvider jwtTokenProvider) {
        this.wishListService = wishListService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/items")
    public ResponseEntity<WishListItemResponseDto> addToWishList(HttpServletRequest request, @RequestBody WishListItemRequestDto wishListItemRequestDto) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getUsernameFromToken(token);
            WishListItemResponseDto responseDto = wishListService.addToWishList(email, wishListItemRequestDto);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Unauthorized
        }
    }

}
